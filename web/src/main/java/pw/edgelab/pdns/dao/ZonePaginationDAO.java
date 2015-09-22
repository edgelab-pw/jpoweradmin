/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * 
 * Control panel for PowerDNS (http://powerdns.com)
 * Copyright (C) 2010 nicmus inc.
 * Jivko Sabev (jivko.sabev@gmail.com) jsabev@nicmus.com
 * 
 * @author jsabev 
 */
package pw.edgelab.pdns.dao;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import pw.edgelab.pdns.entities.Domain;

/**
 * Pagination DAO for the Zone
 * @author jsabev
 *
 */
@Named("zonePaginationDAO")
public class ZonePaginationDAO implements PaginationDAO<Domain>, Serializable{
	private static final long serialVersionUID = 7426821984747247776L;

	@Inject
	private Logger logger;
	
	@Inject
	private int userId;
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	
	/*
	 * (non-Javadoc)
	 * @see com.nicmus.pdns.PaginationDAO#getNumRecords()
	 */
	@Override
	public int getNumRecords() {
		String sql = "select count(d.id) from Domain d where d.user.id = :userId";
		Query numRecordsQuery = this.entityManager.createQuery(sql);
		numRecordsQuery.setParameter("userId", this.userId);
		return ((Long) numRecordsQuery.getSingleResult()).intValue();
	}

	/* (non-Javadoc)
	 * @see com.nicmus.pdns.dao.PaginationDAO#getNumRecords(java.lang.String, java.lang.String)
	 */
	@Override
	public int getNumRecords(String filterBy, String filterValue) {
		Session session =  (Session) this.entityManager.getDelegate();
		Criteria domainCriteria = session.createCriteria(Domain.class);
		domainCriteria.createCriteria("user").add(Restrictions.idEq(this.userId));
		domainCriteria.add(Restrictions.like(filterBy, filterValue.concat("%")));
		domainCriteria.setProjection(Projections.rowCount());
		return ((Long) domainCriteria.list().get(0)).intValue();
	}



	/*
	 * (non-Javadoc)
	 * @see com.nicmus.pdns.PaginationDAO#getRecord(int)
	 */
	@Override
	public Domain getRecord(int id) {
		return this.entityManager.find(Domain.class, id);
	}

	/* (non-Javadoc)
	 * @see com.nicmus.pdns.dao.PaginationDAO#findObjects(int, int, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Domain> findObjects(int offset, int batchSize, String filterColumn, String filterValue, String sortField, boolean desc) {
		Session session =  (Session) this.entityManager.getDelegate();
		Criteria domainCriteria = session.createCriteria(Domain.class);
		domainCriteria.createCriteria("user").add(Restrictions.idEq(this.userId));
		domainCriteria.setFirstResult(offset);
		domainCriteria.setMaxResults(batchSize);
		domainCriteria.setFetchSize(batchSize);
		
		if(sortField != null){
			if(desc){
				domainCriteria.addOrder(Property.forName(sortField).desc());
			} else {
				domainCriteria.addOrder(Property.forName(sortField).asc());
			}
		}
		
		if(filterColumn != null && !filterColumn.isEmpty() && filterValue != null && !filterValue.isEmpty()){
			domainCriteria.add(Restrictions.like(filterColumn, filterValue.concat("%")));
		}
		
		return domainCriteria.list();
	}

	/*
	 * (non-Javadoc)
	 * @see com.nicmus.pdns.PaginationDAO#update(java.lang.Object)
	 */
	@Override
	public void update(Domain domain) {
		this.entityManager.persist(domain);
	}

	
	
	/* (non-Javadoc)
	 * @see com.nicmus.pdns.PaginationDAO#refresh(java.lang.Object)
	 */
	@Override
	public void refresh(Domain t) {
		this.entityManager.refresh(t);
	}

	/* (non-Javadoc)
	 * @see com.nicmus.pdns.dao.PaginationDAO#delete(java.lang.Object)
	 */
	@Override
	public void delete(Domain domain) {
		this.entityManager.remove(domain);
	}


}
