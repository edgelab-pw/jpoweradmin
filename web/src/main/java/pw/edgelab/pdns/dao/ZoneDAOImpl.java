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

import pw.edgelab.pdns.SOARecord;
import pw.edgelab.pdns.entities.Domain;
import pw.edgelab.pdns.entities.Record;
import pw.edgelab.pdns.entities.User;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


@Named("zoneDAO")
public class ZoneDAOImpl implements ZoneDAO{
	private static final long serialVersionUID = -8311863447159727785L;

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private Logger logger;
	

	/* (non-Javadoc)
	 * @see com.nicmus.pdns.dao.ZoneDAO#getUser(int)
	 */
	@Override
	public User getUser(int userId) {
		return this.entityManager.find(User.class, userId);
	}

	/* (non-Javadoc)
	 * @see com.nicmus.pdns.dao.ZoneDAO#updateObject(java.lang.Object)
	 */
	@Override
	public void updateObject(Object object) {
		this.entityManager.merge(object);
	}

	/*
	 * (non-Javadoc)
	 * @see com.nicmus.pdns.dao.ZoneDAO#createDomain(com.nicmus.pdns.entities.Domain, int)
	 */
	@Override
	public void createDomain(Domain domain, int userId){
		//find the user
		User user = this.entityManager.find(User.class, userId);
		domain.setUser(user);
		this.entityManager.persist(user);
		this.entityManager.persist(domain);
	}

	/* (non-Javadoc)
	 * @see com.nicmus.pdns.dao.ZoneDAO#getDomain(java.lang.String)
	 */
	@Override
	public Domain getDomain(String domainName) {
		Query domainQuery = this.entityManager.createQuery("from Domain d where d.name = :domainName");
		domainQuery.setParameter("domainName", domainName);
		try {
			Domain domain = (Domain) domainQuery.getSingleResult();
			this.entityManager.refresh(domain);
			return domain;
		} catch (NoResultException e){
			//silently fail
		}
		return null;
	}

	

	/* (non-Javadoc)
	 * @see com.nicmus.pdns.dao.ZoneDAO#createSOA(int)
	 */
	@Override
	public void createSOA(String domainName) {

		//load the domain
		Query domainQuery = this.entityManager.createQuery("from Domain d where d.name = :name");
		domainQuery.setParameter("name",domainName);
		Domain domain = (Domain) domainQuery.getSingleResult();
		
		SOARecord soaRecordFields = new SOARecord();
		soaRecordFields.setHostmaster("hostmaster.".concat(domain.getName()));

		//create the soa record for the zone
		Record soa = new Record();
		soa.setType(Record.DNSType.SOA);
		soa.setName(domain.getName());
		soa.setContent(soaRecordFields.toString());
		soa.setDomain(domain);
		domain.getRecords().add(soa);
		
		
		this.entityManager.persist(soa);
		
		//create the two ns records
		Record ns1 = new Record();
		Record ns2 = new Record();
		Record ns3 = new Record();
		
		ns1.setName(domain.getName());
		ns2.setName(domain.getName());
		ns3.setName(domain.getName());
		
		ns1.setDomain(domain);
		ns2.setDomain(domain);
		ns3.setDomain(domain);
		
		this.entityManager.persist(ns1);
		this.entityManager.persist(ns2);
		this.entityManager.persist(ns3);
		this.entityManager.persist(domain);
		
	}

	/* (non-Javadoc)
	 * @see com.nicmus.pdns.dao.ZoneDAO#convertToMaster(com.nicmus.pdns.entities.Domain)
	 */
	@Override
	public void convertToMaster(String domainName) {
		//delete the SOA record for the given domain
		Domain domain = this.getDomain(domainName);
		Query deleteRecordsQuery = this.entityManager.createQuery("delete from Record r where r.type = :type and r.domain = :domain");
		deleteRecordsQuery.setParameter("domain", domain);
		deleteRecordsQuery.setParameter("type", Record.DNSType.SOA);
		deleteRecordsQuery.executeUpdate();
		this.logger.info("Deleted SOA Record for domain " + domainName);
		//delete the NS records for the given domain
		deleteRecordsQuery.setParameter("type", Record.DNSType.NS);
		deleteRecordsQuery.executeUpdate();
		this.logger.info("Deleted NS Records for domain " + domainName);
		
		
		this.createSOA(domainName);
		
		//update all the records
		Query updateRecordsQuery = this.entityManager.createQuery("update Record r set r.dateCreated= :now, r.dateModified = :now where r.domain = :domain");
		updateRecordsQuery.setParameter("now", new Date());
		updateRecordsQuery.setParameter("domain", domain);
		updateRecordsQuery.executeUpdate();
	}

	/* (non-Javadoc)
	 * @see com.nicmus.pdns.dao.ZoneDAO#getSOARecord(com.nicmus.pdns.entities.Domain)
	 */
	@Override
	public Record getSOARecord(String domainName) {
		Query soaRecordQuery = this.entityManager.createQuery("from Record r where r.domain.name = :domainName and r.type = :type");
		soaRecordQuery.setParameter("domainName", domainName);
		soaRecordQuery.setParameter("type", Record.DNSType.SOA);
		
		try {
			Record soaRecord = (Record) soaRecordQuery.getSingleResult();
			return soaRecord;
		} catch (NoResultException e){
			//silently fail;
		}
		return null;
		
	}


	/* (non-Javadoc)
	 * @see com.nicmus.pdns.dao.ZoneDAO#getRecords(java.lang.String, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Record> getRecords(String domainName, int offset, int count) {
		String sql = "from Record r where r.domain.name = :domainName";
		Query recordsQuery = this.entityManager.createQuery(sql);
		recordsQuery.setParameter("domainName", domainName);
		recordsQuery.setFirstResult(offset);
		recordsQuery.setMaxResults(count);
		return recordsQuery.getResultList();
	}

	/* (non-Javadoc)
	 * @see com.nicmus.pdns.dao.ZoneDAO#getNumRecords(int)
	 */
	@Override
	public int getNumRecords(String domainName) {
		String sql = "select count(r.id) from Record r where r.domain.name = :domainName";
		Query numRecordsQuery = this.entityManager.createQuery(sql);
		numRecordsQuery.setParameter("domainName", domainName);
		return ((Long)numRecordsQuery.getSingleResult()).intValue();
	}

	
	
}
