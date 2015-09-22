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

import pw.edgelab.pdns.entities.Record;

/**
 * Manage the creationg of records
 * @author jsabev
 *
 */
public interface RecordDAO {
	
	/**
	 * 
	 * @param record
	 * @param domainId
	 */
	public abstract void createRecord(Record record, int domainId);
	
	/**
	 * Determine if the given record exists already in the database
	 * @param name the name of the record - the left hand side of the DNS
	 * @param content the content of the record -> the right hand side of the DNS 
	 * @param type the DNS type of the record
	 * @param domainId the id of the domain records to check
	 * @return true if such a record already exists, false otherwise
	 */
	public boolean recordExists(String name, String content, Record.DNSType type, int domainId);
	
	/**
	 * Determine whether the given record name exists
	 * @param name the name of the record
	 * @param type the record type
	 * @param domainId the domain id of the domain records to check
	 * @return true if such a record exists
	 */
	public boolean recordNameExists(String name, Record.DNSType type, int domainId);
	
	/**
	 * Determine if a record having a content matching the record content passed exists in the db
	 * @param content the name of the record to search in the database
	 * @param type the type of record to search in the database
	 * @param domainId the domainId of the records to check
	 * @return true if a record exists having content = content, false otherwise
	 */
	public boolean recordContentExists(String content, Record.DNSType type, int domainId);
	
	/**
	 * Increment the SOA Serial for the given domain id
	 * @param domainId
	 */
	public void incrementSOASerial(int domainId);
}
