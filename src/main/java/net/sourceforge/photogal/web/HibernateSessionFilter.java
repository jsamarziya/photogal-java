/*
 *  Copyright 2010 The Photogal Team.
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

package net.sourceforge.photogal.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A request filter which handles the Hibernate session and transaction
 * lifecycle.
 */
public class HibernateSessionFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateSessionFilter.class);

    public static final String DEFAULT_SESSION_FACTORY_BEAN_NAME = "sessionFactory";

    private String sessionFactoryBeanName = DEFAULT_SESSION_FACTORY_BEAN_NAME;

    /**
     * Returns the bean name of the SessionFactory to fetch from Spring's root
     * application context.
     */
    public String getSessionFactoryBeanName() {
        return sessionFactoryBeanName;
    }

    /**
     * Set the bean name of the SessionFactory to fetch from Spring's root
     * application context. Default is "sessionFactory".
     * 
     * @see #DEFAULT_SESSION_FACTORY_BEAN_NAME
     */
    public void setSessionFactoryBeanName(String sessionFactoryBeanName) {
        this.sessionFactoryBeanName = sessionFactoryBeanName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        final SessionFactory sessionFactory = lookupSessionFactory();
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Starting a database transaction");
            }
            sessionFactory.getCurrentSession().beginTransaction();
            filterChain.doFilter(request, response);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Committing the database transaction");
            }
            sessionFactory.getCurrentSession().getTransaction().commit();
        } catch (StaleObjectStateException staleEx) {
            LOGGER.error("This interceptor does not implement optimistic concurrency control!");
            LOGGER.error("Your application will not work until you add compensation actions!");
            throw staleEx;
        } catch (Throwable ex) {
            try {
                if (sessionFactory.getCurrentSession().getTransaction().isActive()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Trying to rollback database transaction after exception");
                    }
                    sessionFactory.getCurrentSession().getTransaction().rollback();
                }
            } catch (Throwable rbEx) {
                LOGGER.error("Could not rollback transaction after exception!", rbEx);
            }
            throw new ServletException(ex);
        }
    }

    /**
     * Look up the SessionFactory that this filter should use.
     * 
     * @return the SessionFactory to use
     */
    private SessionFactory lookupSessionFactory() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Using SessionFactory '" + getSessionFactoryBeanName()
                    + "' for HibernateSessionFilter");
        }
        final WebApplicationContext wac = WebApplicationContextUtils
                .getRequiredWebApplicationContext(getServletContext());
        return (SessionFactory) wac.getBean(getSessionFactoryBeanName(), SessionFactory.class);
    }
}
