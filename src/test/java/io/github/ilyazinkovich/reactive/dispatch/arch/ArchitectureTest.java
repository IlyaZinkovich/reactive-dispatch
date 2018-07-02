package io.github.ilyazinkovich.reactive.dispatch.arch;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

  @Test
  void test() {
    final JavaClasses importedClasses = new ClassFileImporter()
        .importPackages("io.github.ilyazinkovich.reactive.dispatch");

    final ArchRule rule = slices()
        .matching("io.github.ilyazinkovich.reactive.dispatch..")
        .should()
        .beFreeOfCycles();

    rule.check(importedClasses);
  }
}
