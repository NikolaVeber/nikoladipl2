//
// Copyright (C) 2010 Igor Andjelkovic (igor.andjelkovic@gmail.com).
// All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.traceServer.traceStorer;

import java.util.HashMap;
import java.util.Set;

/**
 * ID of {@link Event event's} property. It is used as a key with which the
 * property value is to be associated.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class PropertyID {

  /**
   * Represents property type. <i>Not used in the current version of Trace
   * Server</i>. <br/>
   * <br/>
   * Valid property value types are all the Java primitives (
   * <code>boolean, byte,
   * short, int, long, float, double</code> and <code>char</code>) and
   * <code>java.lang.String</code>s.
   */
  public static enum dataTypeID {
    bboolean, bbyte, sshort, iint, llong, ffloat, ddouble, cchar, sstring
  }

  private static int idCounter = 0;

  private static HashMap<Integer, String> idNameMap = new HashMap<Integer, String>();
  private static HashMap<String, PropertyID> nameIdMap = new HashMap<String, PropertyID>();
  private int id;
  private String name;
  private dataTypeID typeID;

  private PropertyID(String name, PropertyID.dataTypeID typeID) {
    this.name = name;
    this.typeID = typeID;
    if (!nameIdMap.containsKey(name)) {
      this.id = idCounter++;
      idNameMap.put(id, name);
      nameIdMap.put(name, this);
    } else {
      this.id = nameIdMap.get(name).id;
    }
  }

  private PropertyID(String name, int id, PropertyID.dataTypeID typeID) {
    this.name = name;
    this.id = id;
    this.typeID = typeID;
    idNameMap.put(id, name);
    nameIdMap.put(name, this);
  }

  /**
   * PropetyID object is represented by it's <code>name</code>,
   * <code>typeID</code>, and <code>ID</code> (assigned internally). Each
   * <code>name</code> is mapped to a unique ID, i.e. there cannot be more two
   * PropertyIDs with the same name.
   * 
   * @param name
   *          PropertyID's name, if there is PropertyID with the same name, that
   *          one is returned, no new PropertyID is created.
   * @param typeID
   *          property type
   */
  public static PropertyID createPropertyID(String name,
      PropertyID.dataTypeID typeID) {
    PropertyID toReturn;
    if (!nameIdMap.containsKey(name)) {
      toReturn = new PropertyID(name, typeID);
    } else {
      toReturn = nameIdMap.get(name);
    }
    return toReturn;
  }

  /**
   * PropetyID object is represented by it's <code>name</code>,
   * <code>typeID</code>, and <code>ID</code> (assigned internally). Each
   * <code>name</code> is mapped to a unique ID, i.e. there cannot be more two
   * PropertyIDs with the same name. <br/>
   * 
   * Used to recreate PropertyIDs if original objects are not available, e.g.
   * when sending information over the wire. <br/>
   * <br/>
   * See {@link gov.nasa.jpf.traceServer.traceStorer.remote.RemoteTraceStorer
   * RemoteTraceStorer} and
   * {@link gov.nasa.jpf.traceServer.traceStorer.remote.TraceStorerProtocol
   * TraceStorerProtocol}.
   * 
   * @param name
   *          PropertyID's name, if there is PropertyID with the same name, that
   *          one is returned, no new PropertyID is created.
   * @param id
   *          PropertyID's internal ID
   * @param typeID
   *          property type
   */
  public static PropertyID createPropertyID(String name, int id,
      PropertyID.dataTypeID typeID) {
    PropertyID toReturn;
    if (!nameIdMap.containsKey(name)) {
      toReturn = new PropertyID(name, id, typeID);
    } else {
      toReturn = nameIdMap.get(name);
    }
    return toReturn;
  }

  /**
   * Returns the internal ID of this PropertyID.
   * 
   * @return internal ID of this PropertyID
   */
  public int getID() {
    return id;
  }

  /**
   * Returns the name of this PropertyID.
   * 
   * @return name of this PropertyID
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the type of this PropertyID.
   * 
   * @return type of this PropertyID
   */
  public PropertyID.dataTypeID getTypeID() {
    return typeID;
  }

  /**
   * Returns the name of the PropertyID with the name specified by the
   * <code>key</code>.
   * 
   * @param key
   *          internal ID of the PropertyID whose name is returned
   * @return name of the PropertyID mapped to internal ID <code>key</code>
   */
  public static String getNameById(int key) {
    return idNameMap.get(key);
  }

  /**
   * Returns the internal ID of the PropertyID with the name specified by the
   * <code>key</code>.
   * 
   * @param key
   *          name of the PropertyID whose internal ID is returned
   * @return internal ID of the PropertyID to which <code>name</code> is mapped
   */
  public static Integer getIdByName(String key) {
    return nameIdMap.get(key).id;
  }

  /**
   * Returns the PropertyID with the name specified by the <code>key</code> or
   * null if there is no PropertyID with the specified name.
   * 
   * @param key
   *          the key whose associated PropertyID is to be returned
   * @return PropertyID with the name specified by the <code>key</code> or null
   *         if there is no PropertyID with the specified name
   */
  public static PropertyID getPropertyIDByName(String key) {
    return nameIdMap.get(key);
  }

  /**
   * Returns the PropertyID with the internal ID specified by the
   * <code>key</code> or null if there is no PropertyID with the specified
   * internal ID.
   * 
   * @param key
   *          the key whose associated PropertyID is to be returned
   * @return PropertyID with the internal ID specified by the <code>key</code>
   *         or null if there is no PropertyID with the specified internal ID
   */
  public static PropertyID getPropertyIDById(Integer key) {
    return nameIdMap.get(idNameMap.get(key));
  }

  /**
   * Returns a set view of all PropertyID names.
   * 
   * @return a set view of all PropertyID names
   */
  public static Set<String> getNames() {
    return nameIdMap.keySet();
  }

  /**
   * Returns a set view of all PropertyID internal IDs.
   * 
   * @return a set view of all PropertyID internal IDs
   */
  public static Set<Integer> getIds() {
    return idNameMap.keySet();
  }

  /**
   * Returns all the created PropertyIDs.
   * 
   * @return all the created PropertyIDs
   */
  public static Iterable<PropertyID> getAllPropertyIDs() {
    return nameIdMap.values();
  }

}
