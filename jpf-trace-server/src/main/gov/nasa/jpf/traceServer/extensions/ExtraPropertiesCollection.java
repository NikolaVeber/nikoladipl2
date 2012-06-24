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
package gov.nasa.jpf.traceServer.extensions;

import gov.nasa.jpf.traceServer.traceStorer.PropertyID;

/**
 * Custom created collection of PropertyID objects. The PropertyID objects can
 * be defined at any place, it doesn't have to be a "collection" class. It is
 * just easier to have it all at one place.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class ExtraPropertiesCollection {

  public static PropertyID NEW_OBJ_ID = PropertyID.createPropertyID("newObjId",
      PropertyID.dataTypeID.sstring);

  public static PropertyID CONSOLE_PRINTLN_STRING = PropertyID
      .createPropertyID("consolePrintlnString", PropertyID.dataTypeID.sstring);

  public static PropertyID INVOKED_METHOD_COMPLETE_NAME = PropertyID
      .createPropertyID("invokedMethodCompleteName",
          PropertyID.dataTypeID.sstring);

}
