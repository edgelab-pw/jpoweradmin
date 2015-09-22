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
package pw.edgelab.pdns;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import pw.edgelab.pdns.entities.Supermaster;

/**
 * @author jsabev
 *
 */
@Named("superMasterAction")
public class SupermasterActionImpl implements Serializable, SupermasterAction {
	private static final long serialVersionUID = -8545623194412605569L;
	
	@Inject
	private FacesMessages facesMessages;

	private List<Supermaster> supermasters;
	
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	
	@Inject
	private Logger logger;
	
	/* (non-Javadoc)
	 * @see com.nicmus.pdns.SupermasterAction#initSupermasters()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initSupermasters(){
		this.supermasters = this.entityManager.createQuery("from Supermaster s").getResultList();
	}
	
	/* (non-Javadoc)
	 * @see com.nicmus.pdns.SupermasterAction#add(com.nicmus.pdns.entities.Supermaster)
	 */
	@Override
	public void add(Supermaster supermaster){
		if(!this.supermasters.contains(supermaster)){
			this.supermasters.add(supermaster);
			this.entityManager.persist(supermaster);
			this.facesMessages.addFromResourceBundle(Severity.INFO, "SuperMasterService.SupermasterAdded", supermaster.getIp());
			supermaster = (Supermaster)Component.getInstance(Supermaster.class, ScopeType.STATELESS);
		} else {
			this.facesMessages.addFromResourceBundle(Severity.WARN, "SuperMasterService.SupermasterExists", supermaster.getIp());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.nicmus.pdns.SupermasterAction#delete(com.nicmus.pdns.entities.Supermaster)
	 */
	@Override
	public void delete(Supermaster supermaster){
		this.supermasters.remove(supermaster);
		this.entityManager.remove(supermaster);
		this.facesMessages.addFromResourceBundle(Severity.INFO, "SuperMasterService.SupermasterDeleted", supermaster.getIp());
	}
}
