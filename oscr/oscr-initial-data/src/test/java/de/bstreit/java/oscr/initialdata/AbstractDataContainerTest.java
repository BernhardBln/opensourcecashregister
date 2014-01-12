package de.bstreit.java.oscr.initialdata;

import java.io.IOException;
import java.lang.reflect.Modifier;

import javax.inject.Named;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;


public class AbstractDataContainerTest {

  private static final Logger logger = LoggerFactory.getLogger(AbstractDataContainer.class);


  @Test
  public void checkConsistency() throws Exception {
    final ImmutableSet<ClassInfo> allClassesBelow = getAllClassesBelow();


    for (ClassInfo ci : allClassesBelow) {
      logger.info("Checking " + ci);
      if (isSubclassOfAbstractDataContainer(ci)) {
        logger.info("  is subclass: " + ci);
        makeSureIsNamed(ci);
      }
    }
  }

  /**
   * @param ci
   * @return
   */
  private boolean isSubclassOfAbstractDataContainer(ClassInfo ci) {
    final Class<?> otherClass = ci.load();

    boolean isNotAbstract = !Modifier.isAbstract(otherClass.getModifiers());
    boolean isAssignable = AbstractDataContainer.class.isAssignableFrom(otherClass);

    return isNotAbstract && isAssignable;
  }

  private void makeSureIsNamed(ClassInfo ci) {
    logger.info("Checking class " + ci.getName());
    if (!ci.load().isAnnotationPresent(Named.class)) {
      throw new RuntimeException(
          "Configuration error: class "
              + ci.getName()
              + " has no @javax.inject.Named-annotation! "
              + "The contents of this class will not be loaded into the database!");
    }
  }


  private ImmutableSet<ClassInfo> getAllClassesBelow() throws IOException {
    final ClassPath cp = ClassPath.from(getClass().getClassLoader());
    final String thisPackageName = getClass().getPackage().getName();

    final ImmutableSet<ClassInfo> allClassesBelow = cp.getTopLevelClassesRecursive(thisPackageName);
    return allClassesBelow;
  }
}
