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

package simash.examples;

import org.apache.log4j.Logger;
import cascading.flow.Flow;
import cascading.operation.regex.RegexSplitter;
import cascading.pipe.*;
import cascading.scheme.TextLine;
import cascading.tap.Hfs;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascalog.StdoutTap;
import clojure.lang.AFn;
import simhash.Simhash;

// java -jar myjar simhash.examples.Simplesimhash "test-resources/test-documents.txt"
public class SimpleSimhash {
    private static final Logger LOG = Logger.getLogger( SimpleSimhash.class );

    public static void main( String[] args ) {
      Tap inputTap = new Hfs( new TextLine( new Fields("line") ), args[0] );
      Tap outputTap = new StdoutTap();

      // parse our input tap. note, we could have used TextDelimited
      // for the input scheme, but I want to show how the operation
      // can be used with a Pipe as well
      Pipe pipe = new Pipe("simhash pipe");
      pipe = new Each(pipe, new Fields("line"), 
                      new RegexSplitter(new Fields("docid", "body"), "\\t"));

      // create a tokenizer IFn
      AFn tokenizer = new AFn() {
          public Object invoke(Object body) throws Exception {
            return "x";
          }
      };

      // create the flow
      Flow simhashFlow = Simhash.simhash(pipe, outputTap, 2, tokenizer);
      simhashFlow.complete(); // or add to your Cascade, etc
    }
}
