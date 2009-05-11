/*
 * ConcourseConnect
 * Copyright 2009 Concursive Corporation
 * http://www.concursive.com
 *
 * This file is part of ConcourseConnect, an open source social business
 * software and community platform.
 *
 * Concursive ConcourseConnect is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, version 3 of the License.
 *
 * Under the terms of the GNU Affero General Public License you must release the
 * complete source code for any application that uses any part of ConcourseConnect
 * (system header files and libraries used by the operating system are excluded).
 * These terms must be included in any work that has ConcourseConnect components.
 * If you are developing and distributing open source applications under the
 * GNU Affero General Public License, then you are free to use ConcourseConnect
 * under the GNU Affero General Public License.
 *
 * If you are deploying a web site in which users interact with any portion of
 * ConcourseConnect over a network, the complete source code changes must be made
 * available.  For example, include a link to the source archive directly from
 * your web site.
 *
 * For OEMs, ISVs, SIs and VARs who distribute ConcourseConnect with their
 * products, and do not license and distribute their source code under the GNU
 * Affero General Public License, Concursive provides a flexible commercial
 * license.
 *
 * To anyone in doubt, we recommend the commercial license. Our commercial license
 * is competitively priced and will eliminate any confusion about how
 * ConcourseConnect can be used and distributed.
 *
 * ConcourseConnect is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ConcourseConnect.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Attribution Notice: ConcourseConnect is an Original Work of software created
 * by Concursive Corporation
 */
package com.concursive.connect.indexer;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

import com.concursive.commons.db.AbstractConnectionPoolTest;

public abstract class AbstractLuceneTest extends TestCase {

  // For testing SnowballAnalyzer
  protected Directory snowballIndex;
  protected IndexWriter snowballWriter;
  private IndexSearcher snowballSearcher;
  protected Analyzer snowballAnalyzer;
  // For testing StandardAnalyzer
  protected Directory standardIndex;
  protected IndexWriter standardWriter;
  private IndexSearcher standardSearcher;
  protected Analyzer standardAnalyzer;

  private static Log log = LogFactory.getLog(AbstractConnectionPoolTest.class);

  protected void setUp() throws Exception {
    // Snowball
    snowballAnalyzer = new SnowballAnalyzer("English");
    snowballIndex = new RAMDirectory();
    snowballWriter = new IndexWriter(snowballIndex, snowballAnalyzer, true);
    // Standard
    standardAnalyzer = new StandardAnalyzer();
    standardIndex = new RAMDirectory();
    standardWriter = new IndexWriter(standardIndex, standardAnalyzer, true);
  }

  protected void tearDown() throws Exception {
    // Stop the index
    if (snowballWriter != null) {
      snowballWriter.close();
    }
    if (snowballSearcher != null) {
      snowballSearcher.close();
    }
    if (snowballIndex != null) {
      snowballIndex.close();
    }
    // Stop the index
    if (standardWriter != null) {
      standardWriter.close();
    }
    if (standardSearcher != null) {
      standardSearcher.close();
    }
    if (standardIndex != null) {
      standardIndex.close();
    }
  }

  protected IndexSearcher getSnowballSearcher() throws IOException {
    // Finish up
    snowballWriter.optimize();
    // Update the shared searcher
    snowballSearcher = new IndexSearcher(snowballIndex);
    return snowballSearcher;
  }

  protected IndexSearcher getStandardSearcher() throws IOException {
    // Finish up
    standardWriter.optimize();
    // Update the shared searcher
    standardSearcher = new IndexSearcher(standardIndex);
    return standardSearcher;
  }
}