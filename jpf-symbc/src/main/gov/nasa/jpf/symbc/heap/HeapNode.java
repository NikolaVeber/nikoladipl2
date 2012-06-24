//
//Copyright (C) 2006 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.
//
//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.
//
//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.symbc.heap;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;


public class HeapNode {

  private int index; // index in JPF's dynamic area
  //private String type; // here we should store the type (fully qualified) of the object represented by this heap cell
  private ClassInfo typeClassInfo;
  private SymbolicInteger  sym_v;

  private HeapNode and; // ref to next HeapNode

  public int getIndex(){
	  return this.index;
  }

  public ClassInfo getType(){
	  return this.typeClassInfo;
  }



  public HeapNode getNext(){
	  return this.and;
  }

  public void setNext(HeapNode next){
	  this.and = next;
  }

  public SymbolicInteger getSymbolic(){
	  return this.sym_v;
  }

  public void setSymbolic(SymbolicInteger sym){
	  this.sym_v = sym;
  }

  public HeapNode(int idx, ClassInfo tClassInfo, SymbolicInteger sym) {
	  index = idx;
	  typeClassInfo = tClassInfo;
	  sym_v = sym;
  }
  
  public void replaceType(ClassInfo tClassInfo) {
	  typeClassInfo = tClassInfo;
  }

  public String toString() {
	    return "[ref=" + index + ", symName=" + sym_v.getName() + ", type=" + typeClassInfo.getName() + "]";
  }


}