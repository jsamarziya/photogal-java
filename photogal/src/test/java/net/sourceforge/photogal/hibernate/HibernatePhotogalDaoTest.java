/*
 *  Copyright 2009 The Photogal Team.
 *  
 *  This file is part of photogal.
 *
 *  photogal is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  photogal is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with photogal.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sourceforge.photogal.hibernate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.ImageDescriptor;

import org.apache.commons.io.IOUtils;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sixcats.utils.FileUtils;

public class HibernatePhotogalDaoTest {
    private final String DB_SCRIPT = "/net/sourceforge/photogal/hsql/db.script";

    private File dbDir;
    private HibernatePhotogalDao dao;
    private SessionFactory sessionFactory;
    private Transaction currentTransaction;

    @BeforeClass
    public static void setUpClass() throws ClassNotFoundException {
        Class.forName("org.hsqldb.jdbcDriver");
    }

    @Before
    public void setUp() throws IOException, SQLException {
        createDatabase();
        sessionFactory = createSessionFactory();
        dao = new HibernatePhotogalDao();
        dao.setSessionFactory(sessionFactory);
        dao.afterPropertiesSet();
        currentTransaction = sessionFactory.getCurrentSession().beginTransaction();
    }

    @After
    public void tearDown() throws SQLException, IOException {
        if (currentTransaction != null) {
            currentTransaction.commit();
        }
        if (sessionFactory != null) {
            final Session currentSession = sessionFactory.getCurrentSession();
            if (currentSession != null && currentSession.isOpen()) {
                currentSession.close();
            }
            sessionFactory.close();
        }
        if (dbDir != null) {
            final Connection connection = getDatabaseConnection();
            try {
                final Statement statement = connection.createStatement();
                statement.execute("SHUTDOWN");
            } finally {
                connection.close();
            }
            org.apache.commons.io.FileUtils.forceDelete(dbDir);
        }
    }

    private void createDatabase() throws IOException, SQLException {
        dbDir = FileUtils.makeTempDirectory("HibernatePhotogalDaoTest", null);
        final InputStream dbScript = getClass().getResourceAsStream(DB_SCRIPT);
        if (dbScript == null) {
            throw new IllegalStateException("Unable to find resource" + DB_SCRIPT);
        }
        String sql;
        try {
            sql = IOUtils.toString(dbScript);
        } finally {
            IOUtils.closeQuietly(dbScript);
        }
        final Connection connection = getDatabaseConnection();
        try {
            final Statement statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            connection.close();
        }
    }

    private SessionFactory createSessionFactory() {
        final InputStream galleryMapping = getClass().getResourceAsStream(
                "/net/sourceforge/photogal/Gallery.hbm.xml");
        if (galleryMapping == null) {
            throw new IllegalArgumentException("unable to find gallery mapping file");
        }
        final InputStream imageDescriptorMapping = getClass().getResourceAsStream(
                "/net/sourceforge/photogal/ImageDescriptor.hbm.xml");
        if (imageDescriptorMapping == null) {
            throw new IllegalArgumentException("unable to find image descriptor mapping file");
        }
        final Configuration config = new Configuration();
        final Properties properties = new Properties();
        properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        properties.put("hibernate.connection.url", getDatabaseURL());
        properties.put("hibernate.connection.username", "sa");
        properties.put("hibernate.connection.password", "");
        properties.put("hibernate.connection.pool_size", 1);
        properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.current_session_context_class", "thread");
        properties.put("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
        config.setProperties(properties);
        config.addInputStream(galleryMapping);
        config.addInputStream(imageDescriptorMapping);
        IOUtils.closeQuietly(galleryMapping);
        IOUtils.closeQuietly(imageDescriptorMapping);
        return config.buildSessionFactory();
    }

    private String getDatabaseURL() {
        return "jdbc:hsqldb:file:" + dbDir.getAbsolutePath() + "/db";
    }

    private Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection(getDatabaseURL(), "sa", "");
    }

    @Test
    public void testGetGalleries() {
        assertThat(dao.getGalleries(false).isEmpty(), is(true));
        assertThat(dao.getGalleries(true).isEmpty(), is(true));
        final Gallery gallery1 = new Gallery();
        gallery1.setOrderIndex(5);
        gallery1.setPublic(false);
        dao.saveOrUpdate(gallery1);
        assertThat(dao.getGalleries(false).isEmpty(), is(true));
        assertThat(dao.getGalleries(true), is(Collections.singletonList(gallery1)));
        final Gallery gallery2 = new Gallery();
        gallery2.setOrderIndex(3);
        gallery2.setPublic(true);
        dao.saveOrUpdate(gallery2);
        assertThat(dao.getGalleries(false), is(Collections.singletonList(gallery2)));
        assertThat(dao.getGalleries(true), is(Arrays.asList(gallery2, gallery1)));
        final Gallery gallery3 = new Gallery();
        gallery3.setOrderIndex(10);
        gallery3.setPublic(true);
        dao.saveOrUpdate(gallery3);
        assertThat(dao.getGalleries(false), is(Arrays.asList(gallery2, gallery3)));
        assertThat(dao.getGalleries(true), is(Arrays.asList(gallery2, gallery1, gallery3)));

    }

    @Test
    public void testGetGalleryCount() {
        assertThat(dao.getGalleryCount(), is(0));
        final Gallery gallery1 = new Gallery();
        dao.saveOrUpdate(gallery1);
        assertThat(dao.getGalleryCount(), is(1));
        final Gallery gallery2 = new Gallery();
        dao.saveOrUpdate(gallery2);
        assertThat(dao.getGalleryCount(), is(2));
        dao.delete(gallery1);
        assertThat(dao.getGalleryCount(), is(1));
        dao.delete(gallery2);
        assertThat(dao.getGalleryCount(), is(0));
    }

    @Test
    public void testGetGallery() {
        assertThat(dao.getGallery(123), is(nullValue()));
        final Gallery gallery = new Gallery();
        dao.saveOrUpdate(gallery);
        assertThat(dao.getGallery(gallery.getId()), is(sameInstance(gallery)));
    }

    @Test
    public void testGetImageDescriptors() {
        assertThat(dao.getImageDescriptors().isEmpty(), is(true));
        final ImageDescriptor descriptor1 = new ImageDescriptor();
        descriptor1.setLocation("");
        dao.saveOrUpdate(descriptor1);
        final ImageDescriptor descriptor2 = new ImageDescriptor();
        descriptor2.setLocation("");
        dao.saveOrUpdate(descriptor2);
        final ImageDescriptor descriptor3 = new ImageDescriptor();
        descriptor3.setLocation("");
        dao.saveOrUpdate(descriptor3);
        final List<ImageDescriptor> descriptors = new ArrayList<ImageDescriptor>();
        descriptors.add(descriptor3);
        descriptors.add(descriptor2);
        descriptors.add(descriptor1);
        Collections.sort(descriptors, new Comparator<ImageDescriptor>() {
            @Override
            public int compare(ImageDescriptor o1, ImageDescriptor o2) {
                return o1 == null ? (o2 == null ? 0 : 1) : o1.getId().compareTo(o2.getId());
            }
        });
        assertThat(dao.getImageDescriptors(), is(descriptors));
    }

    @Test
    public void testGetImageDescriptor() {
        final ImageDescriptor descriptor1 = new ImageDescriptor();
        descriptor1.setLocation("");
        dao.saveOrUpdate(descriptor1);
        final ImageDescriptor descriptor2 = new ImageDescriptor();
        descriptor2.setLocation("foo");
        dao.saveOrUpdate(descriptor2);
        final ImageDescriptor descriptor3 = new ImageDescriptor();
        descriptor3.setLocation("foo/bar");
        dao.saveOrUpdate(descriptor3);
        assertThat(dao.getImageDescriptor(""), is(descriptor1));
        assertThat(dao.getImageDescriptor("foo"), is(descriptor2));
        assertThat(dao.getImageDescriptor("foo/bar"), is(descriptor3));
        assertThat(dao.getImageDescriptor("bar"), is(nullValue()));
        assertThat(dao.getImageDescriptor("someBogusLocation"), is(nullValue()));
    }

    @Test
    public void testGalleryExists() {
        final Gallery gallery = new Gallery();
        dao.saveOrUpdate(gallery);
        assertThat(dao.galleryExists(gallery.getId()), is(true));
        assertThat(dao.galleryExists(gallery.getId() + 1), is(false));
    }

    @Test
    public void testGetImageGalleryCount() {
        final Gallery gallery1 = new Gallery();
        dao.saveOrUpdate(gallery1);
        final ImageDescriptor descriptor1 = new ImageDescriptor();
        descriptor1.setLocation("");
        dao.saveOrUpdate(descriptor1);
        assertThat(dao.getImageGalleryCount(descriptor1.getId()), is(0));
        gallery1.addImage(descriptor1);
        dao.saveOrUpdate(gallery1);
        currentTransaction.commit();
        currentTransaction = sessionFactory.getCurrentSession().beginTransaction();
        assertThat(dao.getImageGalleryCount(descriptor1.getId()), is(1));
    }

}
