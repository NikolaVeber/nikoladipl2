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
package gov.nasa.jpf.traceServer.util;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPFConfigException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility class that is used to configure various parts of the trace server. It
 * can be used even if JPF is not running. Main feature is class instantiation.
 * 
 * @author Igor Andjelkovic
 * 
 */
@SuppressWarnings("serial")
public class TraceServerConfig extends Config {

  public TraceServerConfig(String[] args) {
    super(args);
  }

  static final Class<?>[] NO_ARGTYPES = new Class<?>[0];
  static final Object[] NO_ARGS = new Object[0];
  static final Class<?>[] CONFIG_ARGTYPES = { Config.class };
  final Object[] CONFIG_ARGS = { this };
  //an [optional] hashmap to keep objects we want to be singletons
  HashMap<String,Object> singletons;

  private ClassLoader loader = TraceServerConfig.class.getClassLoader();

  private Class<?> loadClass(String className) throws JPFConfigException {
    if ((className != null) && (className.length() > 0)) {
      try {
        return loader.loadClass(className);
      } catch (ClassNotFoundException cfx) {
        throw new JPFConfigException("class not found " + className);
      } catch (ExceptionInInitializerError ix) {
        throw new JPFConfigException("class initialization of " + className
            + " failed: " + ix, ix);
      }
    }

    return null;
  }

  public <T> T getInstance(String className, Class<T> type,
      Class<?>[] argTypes, Object[] args) throws JPFConfigException {
    Object o = null;
    Constructor<?> ctor = null;

    Class<?> cls = loadClass(className);

    if (cls == null) {
      return null;
    }

    while (o == null) {
      try {
        ctor = cls.getConstructor(argTypes);
        o = ctor.newInstance(args);
      } catch (NoSuchMethodException nmx) {

        if (argTypes.length > 0) {
          // fallback: try the default ctor
          argTypes = NO_ARGTYPES;
          args = NO_ARGS;

        } else {
          // Ok, there is no suitable ctor, bail out
          throw new JPFConfigException(className, cls, "no suitable ctor found");
        }
      } catch (IllegalAccessException iacc) {
        throw new JPFConfigException(className, cls,
            "\n> ctor not accessible: " + getSignatureOfMethod(ctor));
      } catch (IllegalArgumentException iarg) {
        throw new JPFConfigException(className, cls,
            "\n> illegal constructor arguments: " + getSignatureOfMethod(ctor));
      } catch (InvocationTargetException ix) {
        Throwable tx = ix.getTargetException();
        if (tx instanceof JPFConfigException) {
          throw new JPFConfigException(tx.getMessage() + "\n> used within \""
              + className + "\" instantiation of " + cls);
        } else {
          throw new JPFConfigException(className, cls, "\n> exception in "
              + getSignatureOfMethod(ctor) + ":\n>> " + tx, tx);
        }
      } catch (InstantiationException ivt) {
        throw new JPFConfigException(className, cls,
            "\n> abstract class cannot be instantiated");
      } catch (ExceptionInInitializerError eie) {
        throw new JPFConfigException(className, cls,
            "\n> static initialization failed:\n>> " + eie.getException(),
            eie.getException());
      }
    }

    // check type
    if (!type.isInstance(o)) {
      throw new JPFConfigException(className, cls, "\n> instance not of type: "
          + type.getName());
    }

    return type.cast(o); // safe according to above
  }

  private <T> T getInst(String key, Class<?> cls, Class<T> type,
      Class<?>[] argTypes, Object[] args, String id) throws JPFConfigException {
    Object o = null;
    Constructor<?> ctor = null;

    if (cls == null) {
      return null;
    }

    if (id != null) { // check first if we already have this one instantiated as
                      // a singleton
      if (singletons == null) {
        singletons = new HashMap<String, Object>();
      } else {
        o = type.cast(singletons.get(id));
      }
    }

    while (o == null) {
      try {
        ctor = cls.getConstructor(argTypes);
        o = ctor.newInstance(args);
      } catch (NoSuchMethodException nmx) {

        if ((argTypes.length > 1)
            || ((argTypes.length == 1) && (argTypes[0] != Config.class))) {
          // fallback 1: try a single Config param
          argTypes = CONFIG_ARGTYPES;
          args = CONFIG_ARGS;

        } else if (argTypes.length > 0) {
          // fallback 2: try the default ctor
          argTypes = NO_ARGTYPES;
          args = NO_ARGS;

        } else {
          // Ok, there is no suitable ctor, bail out
          throw new JPFConfigException(key, cls, "no suitable ctor found");
        }
      } catch (IllegalAccessException iacc) {
        throw new JPFConfigException(key, cls, "\n> ctor not accessible: "
            + getSignatureOfMethod(ctor));
      } catch (IllegalArgumentException iarg) {
        throw new JPFConfigException(key, cls,
            "\n> illegal constructor arguments: " + getSignatureOfMethod(ctor));
      } catch (InvocationTargetException ix) {
        Throwable tx = ix.getTargetException();
        if (tx instanceof JPFConfigException) {
          throw new JPFConfigException(tx.getMessage() + "\n> used within \""
              + key + "\" instantiation of " + cls);
        } else {
          throw new JPFConfigException(key, cls, "\n> exception in "
              + getSignatureOfMethod(ctor) + ":\n>> " + tx, tx);
        }
      } catch (InstantiationException ivt) {
        throw new JPFConfigException(key, cls,
            "\n> abstract class cannot be instantiated");
      } catch (ExceptionInInitializerError eie) {
        throw new JPFConfigException(key, cls,
            "\n> static initialization failed:\n>> " + eie.getException(),
            eie.getException());
      }
    }

    // check type
    if (!type.isInstance(o)) {
      throw new JPFConfigException(key, cls, "\n> instance not of type: "
          + type.getName());
    }

    if (id != null) { // add to singletons (in case it's not already in there)
      singletons.put(id, o);
    }

    return type.cast(o); // safe according to above
  }

  public <T> ArrayList<T> getInstances(String key, Class<T> type,
      Class<?>[] argTypes, Object[] args) throws JPFConfigException {
    Class<?>[] c = getClasses(key);

    if (c != null) {
      String[] ids = getIDs(key);

      ArrayList<T> a = new ArrayList<T>(c.length);

      for (int i = 0; i < c.length; i++) {
        String id = (ids != null) ? ids[i] : null;
        T listener = getInst(key, c[i], type, argTypes, args, id);
        if (listener != null) {
          a.add(listener);
        } else {
          // should report here
        }
      }

      return a;

    } else {
      // should report here
    }

    return null;
  }

  private String[] getIDs(String key) {
    String v = getProperty(key);

    if (v != null) {
      int i = v.indexOf('@');
      if (i >= 0) { // Ok, we have ids
        String[] a = split(v);
        String[] ids = new String[a.length];
        for (i = 0; i < a.length; i++) {
          ids[i] = getID(a[i]);
        }
        return ids;
      }
    }

    return null;
  }

  private String getID(String v) {
    int i = v.indexOf('@');
    if (i >= 0) {
      return v.substring(i + 1);
    } else {
      return null;
    }
  }

  private String getSignatureOfMethod(Constructor<?> ctor) {
    StringBuilder sb = new StringBuilder(ctor.getName());
    sb.append('(');
    Class<?>[] argTypes = ctor.getParameterTypes();
    for (int i = 0; i < argTypes.length; i++) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append(argTypes[i].getName());
    }
    sb.append(')');
    return sb.toString();
  }
}
