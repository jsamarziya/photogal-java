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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.ImageDescriptor;
import net.sourceforge.photogal.export.PhotogalData;

import org.apache.commons.io.IOUtils;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.sixcats.utils.CalendarDate;
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

    /**
     * Creates a new gallery with the specified order index.
     * 
     * @param orderIndex the order index
     */
    private static Gallery createGallery(int orderIndex) {
        final Gallery retval = new Gallery();
        retval.setOrderIndex(orderIndex);
        return retval;
    }

    /**
     * Creates a new image descriptor.
     */
    private static ImageDescriptor createImageDescriptor() {
        final ImageDescriptor retval = new ImageDescriptor();
        retval.setLocation("");
        return retval;
    }

    @Test
    public void testGetGalleries() {
        assertThat(dao.getGalleries(false).isEmpty(), is(true));
        assertThat(dao.getGalleries(true).isEmpty(), is(true));
        final Gallery gallery1 = createGallery(5);
        gallery1.setPublic(false);
        dao.saveOrUpdate(gallery1);
        assertThat(dao.getGalleries(false).isEmpty(), is(true));
        assertThat(dao.getGalleries(true), is(Collections.singletonList(gallery1)));
        final Gallery gallery2 = createGallery(3);
        gallery2.setPublic(true);
        dao.saveOrUpdate(gallery2);
        assertThat(dao.getGalleries(false), is(Collections.singletonList(gallery2)));
        assertThat(dao.getGalleries(true), is(Arrays.asList(gallery2, gallery1)));
        final Gallery gallery3 = createGallery(10);
        gallery3.setPublic(true);
        dao.saveOrUpdate(gallery3);
        assertThat(dao.getGalleries(false), is(Arrays.asList(gallery2, gallery3)));
        assertThat(dao.getGalleries(true), is(Arrays.asList(gallery2, gallery1, gallery3)));

    }

    @Test
    public void testGetGalleryCount() {
        assertThat(dao.getGalleryCount(false), is(0));
        assertThat(dao.getGalleryCount(true), is(0));
        final Gallery gallery1 = new Gallery();
        gallery1.setPublic(false);
        dao.saveOrUpdate(gallery1);
        assertThat(dao.getGalleryCount(false), is(0));
        assertThat(dao.getGalleryCount(true), is(1));
        final Gallery gallery2 = new Gallery();
        gallery2.setPublic(true);
        dao.saveOrUpdate(gallery2);
        assertThat(dao.getGalleryCount(false), is(1));
        assertThat(dao.getGalleryCount(true), is(2));
        dao.delete(gallery1);
        assertThat(dao.getGalleryCount(false), is(1));
        assertThat(dao.getGalleryCount(true), is(1));
        dao.delete(gallery2);
        assertThat(dao.getGalleryCount(false), is(0));
        assertThat(dao.getGalleryCount(true), is(0));
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
        final ImageDescriptor descriptor1 = createImageDescriptor();
        dao.saveOrUpdate(descriptor1);
        final ImageDescriptor descriptor2 = createImageDescriptor();
        dao.saveOrUpdate(descriptor2);
        final ImageDescriptor descriptor3 = createImageDescriptor();
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
    public void testGetGalleryCountForImage() {
        final Gallery gallery1 = new Gallery();
        dao.saveOrUpdate(gallery1);
        final ImageDescriptor descriptor1 = createImageDescriptor();
        dao.saveOrUpdate(descriptor1);
        assertThat(dao.getGalleryCountForImage(descriptor1.getId()), is(0));
        gallery1.addImage(descriptor1);
        dao.saveOrUpdate(gallery1);
        currentTransaction.commit();
        currentTransaction = sessionFactory.getCurrentSession().beginTransaction();
        assertThat(dao.getGalleryCountForImage(descriptor1.getId()), is(1));
    }

    @Test
    public void testDeleteGallery() {
        final Gallery gallery3 = createGallery(3);
        dao.saveOrUpdate(gallery3);
        final Gallery gallery2 = createGallery(2);
        dao.saveOrUpdate(gallery2);
        final Gallery gallery1 = createGallery(1);
        dao.saveOrUpdate(gallery1);
        currentTransaction.commit();
        currentTransaction = sessionFactory.getCurrentSession().beginTransaction();
        dao.deleteGallery(gallery2.getId(), false);
        currentTransaction.commit();
        currentTransaction = sessionFactory.getCurrentSession().beginTransaction();
        assertThat(dao.getGallery(gallery1.getId()).getOrderIndex(), is(1));
        assertThat(dao.getGallery(gallery3.getId()).getOrderIndex(), is(2));
        assertThat(dao.getGallery(gallery2.getId()), is(nullValue()));
    }

    @Test
    public void testDeleteGalleryWithOrphanImages() {
        Gallery gallery1 = createGallery(1);
        dao.saveOrUpdate(gallery1);
        Gallery gallery2 = createGallery(2);
        dao.saveOrUpdate(gallery2);
        // descriptor0 is in no galleries
        ImageDescriptor descriptor0 = createImageDescriptor();
        dao.saveOrUpdate(descriptor0);
        // descriptor1 is in gallery1
        ImageDescriptor descriptor1 = createImageDescriptor();
        dao.saveOrUpdate(descriptor1);
        gallery1.addImage(descriptor1);
        // descriptor2 is in gallery2
        ImageDescriptor descriptor2 = createImageDescriptor();
        dao.saveOrUpdate(descriptor2);
        gallery2.addImage(descriptor2);
        // descriptor12 is in gallery1 and gallery2
        ImageDescriptor descriptor12 = createImageDescriptor();
        dao.saveOrUpdate(descriptor12);
        gallery1.addImage(descriptor12);
        gallery2.addImage(descriptor12);
        currentTransaction.commit();
        currentTransaction = sessionFactory.getCurrentSession().beginTransaction();

        gallery1 = dao.getGallery(gallery1.getId());
        gallery2 = dao.getGallery(gallery2.getId());
        descriptor0 = dao.getImageDescriptor(descriptor0.getId());
        descriptor1 = dao.getImageDescriptor(descriptor1.getId());
        descriptor2 = dao.getImageDescriptor(descriptor2.getId());
        descriptor12 = dao.getImageDescriptor(descriptor12.getId());
        assertThat(gallery1.getImages(), is(Arrays.asList(descriptor1, descriptor12)));
        assertThat(gallery2.getImages(), is(Arrays.asList(descriptor2, descriptor12)));
        assertThat(descriptor0.getGalleries(), is(Collections.<Gallery> emptySet()));
        assertThat(descriptor1.getGalleries(), is(Collections.singleton(gallery1)));
        assertThat(descriptor2.getGalleries(), is(Collections.singleton(gallery2)));
        assertThat(descriptor12.getGalleries(), is((Object) new HashSet<Gallery>(Arrays.asList(
                gallery1, gallery2))));

        dao.deleteGallery(gallery1.getId(), true);
        currentTransaction.commit();
        currentTransaction = sessionFactory.getCurrentSession().beginTransaction();

        gallery1 = dao.getGallery(gallery1.getId());
        gallery2 = dao.getGallery(gallery2.getId());
        descriptor0 = dao.getImageDescriptor(descriptor0.getId());
        descriptor1 = dao.getImageDescriptor(descriptor1.getId());
        descriptor2 = dao.getImageDescriptor(descriptor2.getId());
        descriptor12 = dao.getImageDescriptor(descriptor12.getId());
        assertThat(gallery1, is(nullValue()));
        assertThat(gallery2.getImages(), is(Arrays.asList(descriptor2, descriptor12)));
        assertThat(descriptor0.getGalleries(), is(Collections.<Gallery> emptySet()));
        assertThat(descriptor1, is(nullValue()));
        assertThat(descriptor2.getGalleries(), is(Collections.singleton(gallery2)));
        assertThat(descriptor12.getGalleries(), is(Collections.singleton(gallery2)));
    }

    @Test
    public void testMoveGallery() {
        final Gallery galleryA = createGallery(1);
        final Gallery galleryB = createGallery(2);
        final Gallery galleryC = createGallery(3);
        final Gallery galleryD = createGallery(4);
        dao.saveOrUpdate(galleryA, galleryB, galleryC, galleryD);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryA, galleryB, galleryC, galleryD)));
        dao.moveGallery(1, 1);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryA, galleryB, galleryC, galleryD)));
        dao.moveGallery(1, 2);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryB, galleryA, galleryC, galleryD)));
        dao.moveGallery(1, 3);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryA, galleryC, galleryB, galleryD)));
        dao.moveGallery(1, 4);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryC, galleryB, galleryD, galleryA)));
        dao.moveGallery(2, 1);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryB, galleryC, galleryD, galleryA)));
        dao.moveGallery(2, 2);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryB, galleryC, galleryD, galleryA)));
        dao.moveGallery(2, 3);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryB, galleryD, galleryC, galleryA)));
        dao.moveGallery(2, 4);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryB, galleryC, galleryA, galleryD)));
        dao.moveGallery(3, 1);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryA, galleryB, galleryC, galleryD)));
        dao.moveGallery(3, 2);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryA, galleryC, galleryB, galleryD)));
        dao.moveGallery(3, 3);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryA, galleryC, galleryB, galleryD)));
        dao.moveGallery(3, 4);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryA, galleryC, galleryD, galleryB)));
        dao.moveGallery(4, 1);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryB, galleryA, galleryC, galleryD)));
        dao.moveGallery(4, 2);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryB, galleryD, galleryA, galleryC)));
        dao.moveGallery(4, 3);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryB, galleryD, galleryC, galleryA)));
        dao.moveGallery(4, 4);
        assertThat(dao.getGalleries(true),
                is(Arrays.asList(galleryB, galleryD, galleryC, galleryA)));
    }

    /**
     * Tests getKeywords(), getImageCountForKeyword(),
     * getImageDescriptors(String, boolean, int, int).
     */
    @Test
    public void testGetByKeyword() {
        final Gallery publicGallery = createGallery(1);
        final Gallery privateGallery = createGallery(2);
        publicGallery.setPublic(true);
        privateGallery.setPublic(false);
        final ImageDescriptor descriptor1 = createImageDescriptor();
        descriptor1.setKeywordsAsString("foo bar baz moo");
        final ImageDescriptor descriptor2 = createImageDescriptor();
        descriptor2.setKeywordsAsString("foo baz too");
        final ImageDescriptor descriptor12 = createImageDescriptor();
        descriptor12.setKeywordsAsString("foo bar goo");
        publicGallery.addImage(descriptor1);
        privateGallery.addImage(descriptor2);
        publicGallery.addImage(descriptor12);
        privateGallery.addImage(descriptor12);
        dao.saveOrUpdate(descriptor1, descriptor2, descriptor12, publicGallery, privateGallery);

        final Map<String, Integer> publicKeywords = dao.getKeywords(false);
        assertThat(publicKeywords.size(), is(5));
        assertThat(publicKeywords.get("foo"), is(2));
        assertThat(publicKeywords.get("bar"), is(2));
        assertThat(publicKeywords.get("baz"), is(1));
        assertThat(publicKeywords.get("moo"), is(1));
        assertThat(publicKeywords.get("too"), is(nullValue()));
        assertThat(publicKeywords.get("goo"), is(1));

        final Map<String, Integer> allKeywords = dao.getKeywords(true);
        assertThat(allKeywords.size(), is(6));
        assertThat(allKeywords.get("foo"), is(3));
        assertThat(allKeywords.get("bar"), is(2));
        assertThat(allKeywords.get("baz"), is(2));
        assertThat(allKeywords.get("moo"), is(1));
        assertThat(allKeywords.get("too"), is(1));
        assertThat(allKeywords.get("goo"), is(1));

        assertThat(dao.getImageCountForKeyword("foo", false), is(2));
        assertThat(dao.getImageCountForKeyword("bar", false), is(2));
        assertThat(dao.getImageCountForKeyword("baz", false), is(1));
        assertThat(dao.getImageCountForKeyword("moo", false), is(1));
        assertThat(dao.getImageCountForKeyword("too", false), is(0));
        assertThat(dao.getImageCountForKeyword("goo", false), is(1));
        assertThat(dao.getImageCountForKeyword("crud", false), is(0));

        assertThat(dao.getImageCountForKeyword("foo", true), is(3));
        assertThat(dao.getImageCountForKeyword("bar", true), is(2));
        assertThat(dao.getImageCountForKeyword("baz", true), is(2));
        assertThat(dao.getImageCountForKeyword("moo", true), is(1));
        assertThat(dao.getImageCountForKeyword("too", true), is(1));
        assertThat(dao.getImageCountForKeyword("goo", true), is(1));
        assertThat(dao.getImageCountForKeyword("crud", true), is(0));

        assertThat(dao.getImageDescriptors("foo", false, 0, 1), is(Arrays.asList(descriptor1)));
        assertThat(dao.getImageDescriptors("foo", false, 1, 1), is(Arrays.asList(descriptor12)));
        assertThat(dao.getImageDescriptors("foo", false, 2, 1), is(Collections
                .<ImageDescriptor> emptyList()));
        assertThat(dao.getImageDescriptors("foo", false, 0, 2), is(Arrays.asList(descriptor1,
                descriptor12)));
        assertThat(dao.getImageDescriptors("foo", false, 1, 2), is(Arrays.asList(descriptor12)));
        assertThat(dao.getImageDescriptors("foo", false, 0, 5), is(Arrays.asList(descriptor1,
                descriptor12)));
        assertThat(dao.getImageDescriptors("foo", false, 0, Integer.MAX_VALUE), is(Arrays.asList(
                descriptor1, descriptor12)));

        assertThat(dao.getImageDescriptors("foo", true, 0, 1), is(Arrays.asList(descriptor1)));
        assertThat(dao.getImageDescriptors("foo", true, 1, 1), is(Arrays.asList(descriptor2)));
        assertThat(dao.getImageDescriptors("foo", true, 2, 1), is(Arrays.asList(descriptor12)));
        assertThat(dao.getImageDescriptors("foo", true, 3, 1), is(Collections
                .<ImageDescriptor> emptyList()));
        assertThat(dao.getImageDescriptors("foo", true, 0, 2), is(Arrays.asList(descriptor1,
                descriptor2)));
        assertThat(dao.getImageDescriptors("foo", true, 0, 3), is(Arrays.asList(descriptor1,
                descriptor2, descriptor12)));
        assertThat(dao.getImageDescriptors("foo", true, 1, 2), is(Arrays.asList(descriptor2,
                descriptor12)));
        assertThat(dao.getImageDescriptors("foo", true, 0, 5), is(Arrays.asList(descriptor1,
                descriptor2, descriptor12)));
        assertThat(dao.getImageDescriptors("foo", true, 0, Integer.MAX_VALUE), is(Arrays.asList(
                descriptor1, descriptor2, descriptor12)));

        assertThat(dao.getImageDescriptors("crud", false, 0, Integer.MAX_VALUE), is(Collections
                .<ImageDescriptor> emptyList()));
        assertThat(dao.getImageDescriptors("crud", true, 0, Integer.MAX_VALUE), is(Collections
                .<ImageDescriptor> emptyList()));
    }

    /**
     * Tests getImageCreationDates(), getImageCountByDateTaken(),
     * getImageCountByYearTaken(), getImageDescriptorsByDateTaken(),
     * getImageDescriptorsByYearTaken().
     */
    @Test
    public void testGetByImageCreationDate() {
        final Gallery publicGallery = createGallery(1);
        final Gallery privateGallery = createGallery(2);
        publicGallery.setPublic(true);
        privateGallery.setPublic(false);
        ImageDescriptor descriptor1 = createImageDescriptor();
        descriptor1.setImageCreationDate(new CalendarDate(null, null, 2000));
        ImageDescriptor descriptor2 = createImageDescriptor();
        descriptor2.setImageCreationDate(new CalendarDate(null, 2, 2000));
        ImageDescriptor descriptor3 = createImageDescriptor();
        descriptor3.setImageCreationDate(new CalendarDate(1, 2, 2000));
        ImageDescriptor descriptor4 = createImageDescriptor();
        descriptor4.setImageCreationDate(new CalendarDate(null, null, 2001));
        ImageDescriptor descriptor5 = createImageDescriptor();
        descriptor5.setImageCreationDate(new CalendarDate(null, null, 2001));
        ImageDescriptor descriptor6 = createImageDescriptor();
        descriptor6.setImageCreationDate(new CalendarDate(null, null, 2001));
        publicGallery.addImage(descriptor1);
        publicGallery.addImage(descriptor2);
        publicGallery.addImage(descriptor3);
        publicGallery.addImage(descriptor4);
        publicGallery.addImage(descriptor5);
        privateGallery.addImage(descriptor6);
        dao.saveOrUpdate(descriptor1, descriptor2, descriptor3, descriptor4, descriptor5,
                descriptor6, publicGallery, privateGallery);
        currentTransaction.commit();
        currentTransaction = sessionFactory.getCurrentSession().beginTransaction();
        descriptor1 = dao.getImageDescriptor(descriptor1.getId());
        descriptor2 = dao.getImageDescriptor(descriptor2.getId());
        descriptor3 = dao.getImageDescriptor(descriptor3.getId());
        descriptor4 = dao.getImageDescriptor(descriptor4.getId());
        descriptor5 = dao.getImageDescriptor(descriptor5.getId());
        descriptor6 = dao.getImageDescriptor(descriptor6.getId());

        final Map<Long, CalendarDate> publicImageCreationDates = dao.getImageCreationDates(false);
        assertThat(publicImageCreationDates.size(), is(5));
        assertThat(publicImageCreationDates.get(descriptor1.getId()), is(descriptor1
                .getImageCreationDate()));
        assertThat(publicImageCreationDates.get(descriptor2.getId()), is(descriptor2
                .getImageCreationDate()));
        assertThat(publicImageCreationDates.get(descriptor3.getId()), is(descriptor3
                .getImageCreationDate()));
        assertThat(publicImageCreationDates.get(descriptor4.getId()), is(descriptor4
                .getImageCreationDate()));
        assertThat(publicImageCreationDates.get(descriptor5.getId()), is(descriptor5
                .getImageCreationDate()));

        final Map<Long, CalendarDate> allImageCreationDates = dao.getImageCreationDates(true);
        assertThat(allImageCreationDates.size(), is(6));
        assertThat(allImageCreationDates.get(descriptor1.getId()), is(descriptor1
                .getImageCreationDate()));
        assertThat(allImageCreationDates.get(descriptor2.getId()), is(descriptor2
                .getImageCreationDate()));
        assertThat(allImageCreationDates.get(descriptor3.getId()), is(descriptor3
                .getImageCreationDate()));
        assertThat(allImageCreationDates.get(descriptor4.getId()), is(descriptor4
                .getImageCreationDate()));
        assertThat(allImageCreationDates.get(descriptor5.getId()), is(descriptor5
                .getImageCreationDate()));
        assertThat(allImageCreationDates.get(descriptor6.getId()), is(descriptor6
                .getImageCreationDate()));

        assertThat(dao.getImageCountByDateTaken(1999, null, false), is(0));
        assertThat(dao.getImageCountByDateTaken(1999, null, true), is(0));
        assertThat(dao.getImageCountByDateTaken(1999, 2, false), is(0));
        assertThat(dao.getImageCountByDateTaken(1999, 2, true), is(0));
        assertThat(dao.getImageCountByDateTaken(2000, null, false), is(1));
        assertThat(dao.getImageCountByDateTaken(2000, null, true), is(1));
        assertThat(dao.getImageCountByDateTaken(2000, 1, false), is(0));
        assertThat(dao.getImageCountByDateTaken(2000, 1, true), is(0));
        assertThat(dao.getImageCountByDateTaken(2000, 2, false), is(2));
        assertThat(dao.getImageCountByDateTaken(2000, 2, true), is(2));
        assertThat(dao.getImageCountByDateTaken(2001, null, false), is(2));
        assertThat(dao.getImageCountByDateTaken(2001, null, true), is(3));

        assertThat(dao.getImageCountByYearTaken(1999, false), is(0));
        assertThat(dao.getImageCountByYearTaken(1999, true), is(0));
        assertThat(dao.getImageCountByYearTaken(2000, false), is(3));
        assertThat(dao.getImageCountByYearTaken(2000, true), is(3));
        assertThat(dao.getImageCountByYearTaken(2001, false), is(2));
        assertThat(dao.getImageCountByYearTaken(2001, true), is(3));

        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, false, 0, 1), is(Arrays
                .asList(descriptor4)));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, false, 1, 1), is(Arrays
                .asList(descriptor5)));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, false, 2, 1), is(Collections
                .<ImageDescriptor> emptyList()));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, false, 0, 2), is(Arrays.asList(
                descriptor4, descriptor5)));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, false, 1, 2), is(Arrays
                .asList(descriptor5)));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, false, 0, 5), is(Arrays.asList(
                descriptor4, descriptor5)));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, false, 0, Integer.MAX_VALUE),
                is(Arrays.asList(descriptor4, descriptor5)));
        assertThat(dao.getImageDescriptorsByDateTaken(2000, 2, false, 0, Integer.MAX_VALUE),
                is(Arrays.asList(descriptor2, descriptor3)));
        assertThat(dao.getImageDescriptorsByDateTaken(1999, null, false, 0, Integer.MAX_VALUE),
                is(Collections.<ImageDescriptor> emptyList()));

        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, true, 0, 1), is(Arrays
                .asList(descriptor4)));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, true, 1, 1), is(Arrays
                .asList(descriptor5)));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, true, 2, 1), is(Arrays
                .asList(descriptor6)));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, true, 3, 1), is(Collections
                .<ImageDescriptor> emptyList()));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, true, 0, 2), is(Arrays.asList(
                descriptor4, descriptor5)));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, true, 0, 3), is(Arrays.asList(
                descriptor4, descriptor5, descriptor6)));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, true, 1, 2), is(Arrays.asList(
                descriptor5, descriptor6)));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, true, 2, 2), is(Arrays
                .asList(descriptor6)));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, true, 0, 5), is(Arrays.asList(
                descriptor4, descriptor5, descriptor6)));
        assertThat(dao.getImageDescriptorsByDateTaken(2001, null, true, 0, Integer.MAX_VALUE),
                is(Arrays.asList(descriptor4, descriptor5, descriptor6)));
        assertThat(dao.getImageDescriptorsByDateTaken(2000, 2, true, 0, Integer.MAX_VALUE),
                is(Arrays.asList(descriptor2, descriptor3)));
        assertThat(dao.getImageDescriptorsByDateTaken(1999, null, true, 0, Integer.MAX_VALUE),
                is(Collections.<ImageDescriptor> emptyList()));

        assertThat(dao.getImageDescriptorsByYearTaken(2001, false, 0, 1), is(Arrays
                .asList(descriptor4)));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, false, 1, 1), is(Arrays
                .asList(descriptor5)));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, false, 2, 1), is(Collections
                .<ImageDescriptor> emptyList()));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, false, 0, 2), is(Arrays.asList(
                descriptor4, descriptor5)));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, false, 1, 2), is(Arrays
                .asList(descriptor5)));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, false, 0, 5), is(Arrays.asList(
                descriptor4, descriptor5)));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, false, 0, Integer.MAX_VALUE), is(Arrays
                .asList(descriptor4, descriptor5)));
        assertThat(dao.getImageDescriptorsByYearTaken(2000, false, 0, Integer.MAX_VALUE), is(Arrays
                .asList(descriptor1, descriptor2, descriptor3)));
        assertThat(dao.getImageDescriptorsByYearTaken(1999, false, 0, Integer.MAX_VALUE),
                is(Collections.<ImageDescriptor> emptyList()));

        assertThat(dao.getImageDescriptorsByYearTaken(2001, true, 0, 1), is(Arrays
                .asList(descriptor4)));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, true, 1, 1), is(Arrays
                .asList(descriptor5)));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, true, 2, 1), is(Arrays
                .asList(descriptor6)));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, true, 3, 1), is(Collections
                .<ImageDescriptor> emptyList()));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, true, 0, 2), is(Arrays.asList(
                descriptor4, descriptor5)));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, true, 0, 3), is(Arrays.asList(
                descriptor4, descriptor5, descriptor6)));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, true, 1, 2), is(Arrays.asList(
                descriptor5, descriptor6)));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, true, 2, 2), is(Arrays
                .asList(descriptor6)));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, true, 0, 5), is(Arrays.asList(
                descriptor4, descriptor5, descriptor6)));
        assertThat(dao.getImageDescriptorsByYearTaken(2001, true, 0, Integer.MAX_VALUE), is(Arrays
                .asList(descriptor4, descriptor5, descriptor6)));
        assertThat(dao.getImageDescriptorsByYearTaken(2000, true, 0, Integer.MAX_VALUE), is(Arrays
                .asList(descriptor1, descriptor2, descriptor3)));
        assertThat(dao.getImageDescriptorsByYearTaken(1999, true, 0, Integer.MAX_VALUE),
                is(Collections.<ImageDescriptor> emptyList()));
    }

    /**
     * Tests getDescriptorCreationDates(), getImageCountByDatePosted(),
     * getImageDescriptorsByDatePosted().
     */
    @Test
    public void testGetByDescriptorCreationDate() throws InterruptedException {
        final Gallery publicGallery = createGallery(1);
        final Gallery privateGallery = createGallery(2);
        publicGallery.setPublic(true);
        privateGallery.setPublic(false);
        ImageDescriptor descriptor1 = createImageDescriptor();
        descriptor1.setCreationDate(new Date(20000));
        ImageDescriptor descriptor2 = createImageDescriptor();
        descriptor2.setCreationDate(new Date(40000));
        ImageDescriptor descriptor12 = createImageDescriptor();
        descriptor12.setCreationDate(new Date(60000));
        publicGallery.addImage(descriptor1);
        privateGallery.addImage(descriptor2);
        publicGallery.addImage(descriptor12);
        privateGallery.addImage(descriptor12);
        dao.saveOrUpdate(descriptor1, descriptor2, descriptor12, publicGallery, privateGallery);
        currentTransaction.commit();
        currentTransaction = sessionFactory.getCurrentSession().beginTransaction();
        descriptor1 = dao.getImageDescriptor(descriptor1.getId());
        descriptor2 = dao.getImageDescriptor(descriptor2.getId());
        descriptor12 = dao.getImageDescriptor(descriptor12.getId());

        final Map<Long, Date> publicDescriptorCreationDates = dao.getDescriptorCreationDates(false);
        assertThat(publicDescriptorCreationDates.size(), is(2));
        assertThat(publicDescriptorCreationDates.get(descriptor1.getId()), is(descriptor1
                .getCreationDate()));
        assertThat(publicDescriptorCreationDates.get(descriptor12.getId()), is(descriptor12
                .getCreationDate()));

        final Map<Long, Date> allDescriptorCreationDates = dao.getDescriptorCreationDates(true);
        assertThat(allDescriptorCreationDates.size(), is(3));
        assertThat(allDescriptorCreationDates.get(descriptor1.getId()), is(descriptor1
                .getCreationDate()));
        assertThat(allDescriptorCreationDates.get(descriptor2.getId()), is(descriptor2
                .getCreationDate()));
        assertThat(allDescriptorCreationDates.get(descriptor12.getId()), is(descriptor12
                .getCreationDate()));

        assertThat(dao.getImageCountByDatePosted(new Date(0), new Date(Long.MAX_VALUE), false),
                is(2));
        assertThat(dao.getImageCountByDatePosted(new Date(0), new Date(Long.MAX_VALUE), true),
                is(3));
        assertThat(dao.getImageCountByDatePosted(new Date(20000), new Date(40000), false), is(1));
        assertThat(dao.getImageCountByDatePosted(new Date(20000), new Date(40000), true), is(2));

        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE),
                false, 0, 1), is(Arrays.asList(descriptor1)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE),
                false, 1, 1), is(Arrays.asList(descriptor12)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE),
                false, 2, 1), is(Collections.<ImageDescriptor> emptyList()));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE),
                false, 0, 2), is(Arrays.asList(descriptor1, descriptor12)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE),
                false, 1, 2), is(Arrays.asList(descriptor12)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE),
                false, 0, 5), is(Arrays.asList(descriptor1, descriptor12)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE),
                false, 0, Integer.MAX_VALUE), is(Arrays.asList(descriptor1, descriptor12)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(20000), new Date(40000), false, 0,
                Integer.MAX_VALUE), is(Arrays.asList(descriptor1)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(0), false, 0,
                Integer.MAX_VALUE), is(Collections.<ImageDescriptor> emptyList()));

        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE), true,
                0, 1), is(Arrays.asList(descriptor1)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE), true,
                1, 1), is(Arrays.asList(descriptor2)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE), true,
                2, 1), is(Arrays.asList(descriptor12)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE), true,
                3, 1), is(Collections.<ImageDescriptor> emptyList()));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE), true,
                0, 2), is(Arrays.asList(descriptor1, descriptor2)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE), true,
                0, 3), is(Arrays.asList(descriptor1, descriptor2, descriptor12)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE), true,
                1, 2), is(Arrays.asList(descriptor2, descriptor12)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE), true,
                2, 2), is(Arrays.asList(descriptor12)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE), true,
                0, 5), is(Arrays.asList(descriptor1, descriptor2, descriptor12)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(Long.MAX_VALUE), true,
                0, Integer.MAX_VALUE), is(Arrays.asList(descriptor1, descriptor2, descriptor12)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(20000), new Date(40000), true, 0,
                Integer.MAX_VALUE), is(Arrays.asList(descriptor1, descriptor2)));
        assertThat(dao.getImageDescriptorsByDatePosted(new Date(0), new Date(0), true, 0,
                Integer.MAX_VALUE), is(Collections.<ImageDescriptor> emptyList()));
    }

    @Test
    public void testGetEmptyData() {
        final long startTime = System.currentTimeMillis();
        final PhotogalData pgData = dao.getData();
        final long endTime = System.currentTimeMillis();
        final long exportTime = pgData.getExportDate().getTime();
        assertThat(exportTime >= startTime, is(true));
        assertThat(exportTime <= endTime, is(true));
        assertThat(pgData.getGalleries().isEmpty(), is(true));
        assertThat(pgData.getImageDescriptors().isEmpty(), is(true));
    }

    @Test
    public void testGetData() {
        final Gallery gallery1 = createGallery(1);
        final Gallery gallery2 = createGallery(2);
        final ImageDescriptor descriptor1 = createImageDescriptor();
        final ImageDescriptor descriptor2 = createImageDescriptor();
        final ImageDescriptor descriptor12 = createImageDescriptor();
        gallery1.addImage(descriptor1);
        gallery2.addImage(descriptor2);
        gallery1.addImage(descriptor12);
        gallery2.addImage(descriptor12);
        dao.saveOrUpdate(gallery1, gallery2, descriptor1, descriptor2, descriptor12);
        final PhotogalData pgData = dao.getData();
        assertThat(pgData.getGalleries(), is(Arrays.asList(gallery1, gallery2)));
        assertThat(pgData.getImageDescriptors(), is(Arrays.asList(descriptor1, descriptor2,
                descriptor12)));
    }

    @Test
    public void testDelete() {
        final ImageDescriptor descriptor1 = createImageDescriptor();
        final ImageDescriptor descriptor2 = createImageDescriptor();
        final ImageDescriptor descriptor3 = createImageDescriptor();
        assertThat(dao.getImageDescriptors().isEmpty(), is(true));
        dao.delete(descriptor1);
        assertThat(dao.getImageDescriptors().isEmpty(), is(true));
        dao.saveOrUpdate(descriptor1);
        assertThat(dao.getImageDescriptors(), is(Arrays.asList(descriptor1)));
        dao.delete(descriptor1);
        assertThat(dao.getImageDescriptors().isEmpty(), is(true));
        dao.saveOrUpdate(descriptor2, descriptor3);
        assertThat(dao.getImageDescriptors(), is(Arrays.asList(descriptor2, descriptor3)));
        dao.delete(descriptor2);
        assertThat(dao.getImageDescriptors(), is(Arrays.asList(descriptor3)));
        dao.delete(descriptor3);
        assertThat(dao.getImageDescriptors().isEmpty(), is(true));
    }

    @Test
    @Ignore("not used")
    public void testSaveOrUpdate() {
        // saveOrUpdate() is pretty well tested by the other test methods in
        // this class, so I can't think of anything to add here
    }

    @Test
    @Ignore("not used")
    public void testUpdate() {
        // update() is pretty well tested by the other test methods in this
        // class, so I can't think of anything to add here
    }

    /**
     * An integration test in which we create a new gallery and then add an
     * image to it.
     */
    @Test
    public void testCreateGalleryAndImage() {
        final Gallery gallery = new Gallery();
        gallery.setOrderIndex(5);
        gallery.setName("galleryName");
        gallery.setDescription("galleryDescription");
        gallery.setPublic(true);
        dao.saveOrUpdate(gallery);

        currentTransaction.commit();
        currentTransaction = sessionFactory.getCurrentSession().beginTransaction();

        final ImageDescriptor image = new ImageDescriptor();
        image.setLocation("imageLocation");
        image.setWidth(3);
        image.setHeight(4);
        image.setTitle("imageTitle");
        image.setDescription("imageDescription");
        image.setImageCreationDate(new CalendarDate(11, 13, 15));
        image.setKeywordsAsString("key1 key2 key3");
        dao.saveOrUpdate(image);

        assertThat(gallery.containsImage(image), is(false));
        gallery.addImage(image);
        assertThat(gallery.containsImage(image), is(true));
        dao.saveOrUpdate(gallery);
        assertThat(gallery.containsImage(image), is(true));

        currentTransaction.commit();
        currentTransaction = sessionFactory.getCurrentSession().beginTransaction();

        final Gallery savedGallery = dao.getGallery(gallery.getId());
        assertThat(savedGallery, is(notNullValue()));
        assertThat(savedGallery, is(not(sameInstance(gallery))));
        assertThat(savedGallery.getImageCount(), is(1));

        final ImageDescriptor savedImage = dao.getImageDescriptor(image.getId());
        assertThat(savedImage, is(notNullValue()));
        assertThat(savedImage.getKeywordsAsString(), is("key1 key2 key3"));
        assertThat(savedImage.getKeywords(), is(Arrays.asList("key1", "key2", "key3")));
        assertThat(savedImage.getImageCreationDate(), is(new CalendarDate(11, 13, 15)));

        final Set<Gallery> imageGalleries = savedImage.getGalleries();
        assertThat(imageGalleries, is(notNullValue()));
        assertThat(imageGalleries.size(), is(1));
        assertThat(imageGalleries.contains(savedGallery), is(true));

        assertThat(savedGallery.containsImage(savedImage), is(true));
    }

    @Test
    public void testGetImageDescriptorCount() {
        final Gallery publicGallery = createGallery(1);
        final Gallery privateGallery = createGallery(2);
        publicGallery.setPublic(true);
        privateGallery.setPublic(false);
        final ImageDescriptor descriptor1 = createImageDescriptor();
        final ImageDescriptor descriptor2 = createImageDescriptor();
        final ImageDescriptor descriptor12 = createImageDescriptor();
        publicGallery.addImage(descriptor1);
        privateGallery.addImage(descriptor2);
        publicGallery.addImage(descriptor12);
        privateGallery.addImage(descriptor12);
        dao.saveOrUpdate(descriptor1, descriptor2, descriptor12, publicGallery, privateGallery);
        assertThat(dao.getImageDescriptorCount(false), is(2));
        assertThat(dao.getImageDescriptorCount(true), is(3));
    }
}