/**
 * 
 */
package io.alanda.base.mail;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

/**
 * @author developer
 *
 */
public class Html2PlainTextConverter {

  /**
   * Format an Element to plain-text
   * 
   * @param element the root element to format
   * @return formatted text
   */
  public String getPlainText(Element element) {
    FormattingVisitor formatter = new FormattingVisitor();
    NodeTraversor traversor = new NodeTraversor(formatter);
    traversor.traverse(element); // walk the DOM, and call .head() and
    // .tail() for each node
    String s = formatter.toString();
    s = StringUtils.strip(s, "\u00A0 \n\t\r");
    // s = s.replace("\n\n\n", "\n\n");

    return s;
  }

  // the formatting rules, implemented in a breadth-first DOM traverse
  private class FormattingVisitor implements NodeVisitor {

    private static final int maxWidth = 80;

    private int width = 0;

    private StringBuilder accum = new StringBuilder(); // holds the

    // accumulated text

    // hit when the node is first seen
    public void head(Node node, int depth) {
      String name = node.nodeName();
      if (node instanceof TextNode)
        append(((TextNode) node).text()); // TextNodes carry all
      // user-readable text in the
      // DOM.
      else if (name.equals("li"))
        append("\n * ");
    }

    // hit when all of the node's children (if any) have been visited
    public void tail(Node node, int depth) {
      String name = node.nodeName();
      if (StringUtil.in(name, "br", "tr", "p"))
        append("\n");
      else if (StringUtil.in(name, "h1", "h2", "h3", "h4", "h5"))
        append("\n\n");
      else if (name.equals("a"))
        append(String.format(" <%s>", node.absUrl("href")));
    }

    // appends text to the string builder with a simple word wrap method
    private void append(String text) {
      if (text.startsWith("\n"))
        width = 0; // reset counter if starts with a newline. only from
      // formats above, not in natural text
      if (text.equals(" ") && (accum.length() == 0 || StringUtil.in(accum.substring(accum.length() - 1), " ", "\n")))
        return; // don't accumulate long runs of empty spaces

      if (text.length() + width > maxWidth) { // won't fit, needs to wrap
        String words[] = text.split("\\s+");
        for (int i = 0; i < words.length; i++ ) {
          String word = words[i];
          boolean last = i == words.length - 1;
          if ( !last) // insert a space if not the last word
            word = word + " ";
          if (word.length() + width > maxWidth) { // wrap and reset
            // counter
            accum.append("\n").append(word);
            width = word.length();
          } else {
            accum.append(word);
            width += word.length();
          }
        }
      } else { // fits as is, without need to wrap text
        accum.append(text);
        width += text.length();
      }
    }

    public String toString() {
      return accum.toString();
    }
  }

  public static void main(String[] args) {
    String test = " \nä\nFrom: incident@sca.com [mailto:incident@sca.com] \nSent: Dienstag, 30. April 2013 10:52\nTo: Postl Hannes\nSubject: New Task for Incident # 649154 is assigned to you.\n\nNew Task for Incident 649154 has been assigned to you.\n\nService: PLAIN - 2065\nPriority: Medium\nTarget: 02.05.2013 10:30\n\nSummary:\nAusschuss PLAIN / Report\n\nDetails:\n\n Hallo,\n\n Bitte die alten Ausschussdaten aus der DB löschen. Wir haben seit den letzten \nWochen in beiden PVs erhöhten, meist doppelten Ausschuss.\n\n Danke!\n\n MfG,\n\n\n\n Roland Garherr\n Production Manager Folded Products\n\n\n\n\n SCA HYGIENE PRODUCTS GMBH\n\n Hauptstrasse 1 / Ortmann\n AT-2763 PERNITZ\n Phone +43 2632 707 398\n Fax +43 2632 707 325\n Mobile +43 664 4309195\n roland.garherr@sca.com\n www.sca.com\n\n\n\n\n\n\n\n\nAssigned by: ecdomain\\athperu\n";
    System.out.println("1" + test);
    System.out.println("2" + StringUtils.strip(test, "\u00A0 \n\t\r"));
    System.out.println("3");
  }

}
