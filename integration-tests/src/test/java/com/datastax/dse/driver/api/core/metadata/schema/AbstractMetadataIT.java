/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.dse.driver.api.core.metadata.schema;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.testinfra.session.SessionRule;
import com.datastax.oss.driver.categories.ParallelizableTests;
import java.util.Optional;
import org.junit.experimental.categories.Category;

/* Abstract class to hold common methods for Metadata Schema tests. */
@Category(ParallelizableTests.class)
public abstract class AbstractMetadataIT {

  /**
   * Asserts the presence of a Keyspace and that it's name matches the keyspace associated with the
   * Session Rule.
   */
  protected void assertKeyspace(Optional<KeyspaceMetadata> keyspaceOpt) {
    // assert the keyspace
    assertThat(keyspaceOpt)
        .hasValueSatisfying(
            keyspace -> {
              assertThat(keyspace).isInstanceOf(DseKeyspaceMetadata.class);
              assertThat(keyspace.getName().asInternal())
                  .isEqualTo(getSessionRule().keyspace().asInternal());
            });
  }

  /* Convenience method for executing a CQL statement using the test's Session Rule. */
  public void execute(String cql) {
    getSessionRule()
        .session()
        .execute(
            SimpleStatement.builder(cql)
                .setExecutionProfile(getSessionRule().slowProfile())
                .build());
  }

  /**
   * Convenience method for retrieving the Keyspace metadata from this test's Session Rule. Also
   * asserts the Keyspace exists and has the expected name.
   */
  public DseKeyspaceMetadata getKeyspace() {
    Optional<KeyspaceMetadata> keyspace =
        getSessionRule().session().getMetadata().getKeyspace(getSessionRule().keyspace());
    assertKeyspace(keyspace);
    return ((DseKeyspaceMetadata) keyspace.get());
  }

  /* Concrete ITs should return their ClassRule SessionRule. */
  protected abstract SessionRule<DseSession> getSessionRule();
}