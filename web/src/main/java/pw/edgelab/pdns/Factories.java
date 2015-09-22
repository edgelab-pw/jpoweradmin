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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import pw.edgelab.pdns.entities.Domain;
import pw.edgelab.pdns.entities.Record.DNSType;


/**
 * A collection of various factory providers (for enums)
 * @author jsabev
 *
 */
@Named("factories")
public class Factories {
	
	@Factory("domainTypes")
	public Set<Domain.DomainType> getDomainTypes(){
		return EnumSet.allOf(Domain.DomainType.class);
		
	}
	
	@Factory("allRecordTypes")
	public Set<DNSType> getAllRecordTypes(){
		return EnumSet.allOf(DNSType.class);
	}
	
	@Factory("normalRecordTypes")
	public Set<DNSType> getNormalRecordTypes(){
		Set<DNSType> recordTypes = this.getAllRecordTypes();
		recordTypes.remove(DNSType.SOA);
		return recordTypes;
	}
	
	@Factory("availableTimeZones")
	public String[] getTimeZoneIds(){
		String[] availableIDs = TimeZone.getAvailableIDs();
		Arrays.sort(availableIDs);
		return availableIDs;
	}
	
	@Factory("countries")
	public List<String> getCountries(){
		List<String> countries = new ArrayList<String>();
		String[] isoCountries = Locale.getISOCountries();
		Locale currentLocale = (Locale) Component.getInstance("org.jboss.seam.core.locale");
		for(String countryCode : isoCountries){
			Locale locale = new Locale(currentLocale.getLanguage(),countryCode);
			countries.add(locale.getDisplayCountry());
		}
		return countries;
	}
	
	/**
	 * Return a map of country name -> ISO 3166-2 country code 
	 * @return
	 */
	@Factory("countryMap")
	public Map<String, String> getCountryMap(){
		Locale currentLocale = (Locale) Component.getInstance("org.jboss.seam.core.locale");
		Map<String,String> countriesMap = new LinkedHashMap<String, String>();
		String[] isoCountries = Locale.getISOCountries();
		for(String code : isoCountries){
			Locale locale = new Locale(currentLocale.getLanguage(), code);
			String displayCountry = locale.getDisplayCountry();
			countriesMap.put(displayCountry, code);
		}
		return countriesMap;
	}
}
