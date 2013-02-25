package com.example.common.naming;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * SimpleContext
 * @author Kazuhiko Arase
 */
public class SimpleContext implements Context {

	private Map<Object,Object> map;

	public SimpleContext() {
		map = new HashMap<Object,Object>();
	}

	public void close() throws NamingException {
		throw new NamingException("not implemented.");
	}

	// 名称関連
	
	public Name composeName(Name name, Name prefix) throws NamingException {
		throw new NamingException("not implemented.");
	}
	public String composeName(String name, String prefix) throws NamingException {
		throw new NamingException("not implemented.");
	}
	public void rename(Name oldName, Name newName) throws NamingException {
		throw new NamingException("not implemented.");
	}
	public void rename(String oldName, String newName) throws NamingException {
		throw new NamingException("not implemented.");
	}
	
	// Subcontext 関連
	public Context createSubcontext(Name name) throws NamingException {

		if (name.size() == 1) {
			if (map.containsKey(name) ) {
				throw new NamingException("already binded:" + name);
			}
			Context ctx = new SimpleContext();
			map.put(name, ctx);
			return ctx;
		}
		
		if (!map.containsKey(name.getPrefix(1) ) ) {
			map.put(name.getPrefix(1), new SimpleContext() );
		}
		Context subCtx = (Context)map.get(name.getPrefix(1) );
		return subCtx.createSubcontext(name.getSuffix(1) );
	}
	public Context createSubcontext(String name) throws NamingException {
		return createSubcontext(new CompositeName(name) );
	}

	public void destroySubcontext(Name name) throws NamingException {
		throw new NamingException("not implemented.");
	}
	public void destroySubcontext(String name) throws NamingException {
		throw new NamingException("not implemented.");
	}
	
	// Environment 関連
	
	public Object addToEnvironment(String propName, Object propVal) throws NamingException {
		throw new NamingException("not implemented.");
	}		
	
	public Object removeFromEnvironment(String propName) throws NamingException {
		throw new NamingException("not implemented.");
	}

	public Hashtable<?, ?> getEnvironment() throws NamingException {
		throw new NamingException("not implemented.");
	}

	public String getNameInNamespace() throws NamingException {
		throw new NamingException("not implemented.");
	}

	public NameParser getNameParser(Name name) throws NamingException {
		throw new NamingException("not implemented.");
	}
	public NameParser getNameParser(String name) throws NamingException {
		throw new NamingException("not implemented.");
	}
	
	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
		throw new NamingException("not implemented.");
	}
	public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
		throw new NamingException("not implemented.");
	}
	
	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
		throw new NamingException("not implemented.");
	}
	public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
		throw new NamingException("not implemented.");
	}
	
	public Object lookup(Name name) throws NamingException {
		if (name.size() == 1) {
			if (!map.containsKey(name) ) {
				throw new NamingException("not binded:" + name);
			}
			return map.get(name);
		}
		
		if (!map.containsKey(name.getPrefix(1) ) ) {
			map.put(name.getPrefix(1), new SimpleContext() );
		}
		Context subCtx = (Context)map.get(name.getPrefix(1) );
		return subCtx.lookup(name.getSuffix(1) );
	}
	public Object lookup(String name) throws NamingException {
		return lookup(new CompositeName(name) );
	}
	
	public Object lookupLink(Name name) throws NamingException {
		throw new NamingException("not implemented.");
	}
	public Object lookupLink(String name) throws NamingException {
		throw new NamingException("not implemented.");
	}

	// Bind関連

	public void bind(Name name, Object obj) throws NamingException {
		if (name.size() == 1) {
			if (map.containsKey(name) ) {
				throw new NamingException("already binded:" + name);
			}
			map.put(name, obj);
			return;
		}
		if (!map.containsKey(name.getPrefix(1) ) ) {
			map.put(name.getPrefix(1), new SimpleContext() );
		}
		Context ctx = (Context)map.get(name.getPrefix(1) );
		ctx.bind(name.getSuffix(1), obj);
	}

	public void bind(String name, Object obj) throws NamingException {
		bind(new CompositeName(name), obj);
	}
	
	public void rebind(Name name, Object obj) throws NamingException {
		throw new NamingException("not implemented.");
	}
	public void rebind(String name, Object obj) throws NamingException {
		throw new NamingException("not implemented.");
	}

	public void unbind(Name name) throws NamingException {
		throw new NamingException("not implemented.");
	}
	public void unbind(String name) throws NamingException {
		throw new NamingException("not implemented.");
	}
}
