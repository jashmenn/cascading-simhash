/*
    Copyright 2010 Nate Murray
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
 
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package simhash.examples;

import org.apache.log4j.Logger;
import cascading.flow.Flow;
import cascading.pipe.*;
import cascading.scheme.TextDelimited;
import cascading.tap.Hfs;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascalog.StdoutTap;
import clojure.lang.AFn;
import simhash.Simhash;
import simhash.tokenizers.HtmlText;

/**
 * Html Simhash - an example of how to use Simhash
 *
 * To run this example:
 *   lein uberjar
 *   lein classpath > classpath
 *   java -cp `cat classpath`:build/cascading-simhash-1.0.0-SNAPSHOT-standalone.jar simhash.examples.HtmlSimhash "test-resources/test-html-documents.txt"
 **/
public class HtmlSimhash {
  private static final Logger LOG = Logger.getLogger( HtmlSimhash.class );

  public static void main( String[] args ) {
    Tap inputTap = new Hfs( new TextDelimited( 
                                new Fields("docid", "body"), "	" ),
                            args[0] );
    Tap outputTap = new StdoutTap();

    // create the flow
    Flow simhashFlow = Simhash.simhash(inputTap, outputTap, 1, HtmlText.tokenizer(3));
    simhashFlow.complete(); // or add to your Cascade, etc
  }
}

/*
  88d04488c452260207cd4d171084a5b8a44bbab0
  8cb836a5f1b8e305608a586c23828a02e35b8468

  k
 */
