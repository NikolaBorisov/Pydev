/*
 * Created on Mar 14, 2006
 */
package org.python.pydev.core.docutils;

import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.python.pydev.core.structure.FastStringBuffer;

public class ParsingUtilsTest extends TestCase {

    public static void main(String[] args) {
    	try {
			ParsingUtilsTest test = new ParsingUtilsTest();
			test.setUp();
			test.testMakeParseable();
			test.tearDown();
			junit.textui.TestRunner.run(ParsingUtilsTest.class);
		} catch (Throwable e) {
			e.printStackTrace();
		}
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsInCommOrStr() {
        String str = "" +
                "#comm1\n" +
                "'str'\n" +
                "pass\n" +
                "";
        assertEquals(ParsingUtils.PY_COMMENT, ParsingUtils.getContentType(str, 2));
        assertEquals(ParsingUtils.PY_SINGLELINE_STRING1, ParsingUtils.getContentType(str, 10));
        assertEquals(ParsingUtils.PY_DEFAULT, ParsingUtils.getContentType(str, 17));
    }
    
    public void testIterator() throws Exception {
    	String str = "" +
    	"#c\n" +
    	"'s'\n" +
    	"pass\n" +
    	"";
    	Document d = new Document(str);
    	Iterator it = ParsingUtils.getNoLiteralsOrCommentsIterator(d);
    	assertEquals("\n",it.next());
    	assertEquals(true,it.hasNext());
    	assertEquals("\n",it.next());
    	assertEquals(true,it.hasNext());
    	assertEquals("pass\n",it.next());
    	assertEquals(false,it.hasNext());
	}
    
    public void testIterator2() throws Exception {
    	String str = "" +
    	"#c\n" +
    	"'s'" +
    	"";
    	Document d = new Document(str);
    	PyDocIterator it = (PyDocIterator) ParsingUtils.getNoLiteralsOrCommentsIterator(d);
    	assertEquals(-1, it.getLastReturnedLine());
    	
    	assertEquals("\n",it.next());
    	assertEquals(0, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("",it.next());
    	assertEquals(1, it.getLastReturnedLine());
    	assertEquals(false,it.hasNext());
    }
    
    public void testIterator3() throws Exception {
    	String str = "" +
    	"#c";
    	Document d = new Document(str);
    	PyDocIterator it = (PyDocIterator) ParsingUtils.getNoLiteralsOrCommentsIterator(d);
    	assertEquals(-1, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("",it.next());
    	assertEquals(0, it.getLastReturnedLine());
    	assertEquals(false,it.hasNext());
    }
    
    
    public void testIterator5() throws Exception {
    	String str = "" +
    	"class Foo:\n" +
    	"    '''\n" +
    	"    \"\n"+
    	"    b\n"+
    	"    '''a\n"+
    	"    pass\n"+
    	"\n";
    	Document d = new Document(str);
    	PyDocIterator it = new PyDocIterator(d, false, true, true);
    	assertEquals(-1, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("class Foo:",it.next());
    	assertEquals(0, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("       ",it.next());
    	assertEquals(1, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("     ",it.next());
    	assertEquals(2, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("     ",it.next());
    	assertEquals(3, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("       a",it.next());
    	assertEquals(4, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("    pass",it.next());
    	assertEquals(5, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("",it.next());
    	assertEquals(6, it.getLastReturnedLine());
    	assertEquals(false,it.hasNext());
    }
    
    
    
    public void testIterator7() throws Exception {
    	String str = "" +
    	"'''\n" +
    	"\n" +
    	"'''\n" +
    	"";
    	Document d = new Document(str);
    	PyDocIterator it = new PyDocIterator(d, false, true, true);
    	
    	assertEquals("   ",it.next());
    	assertEquals(0, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("",it.next());
    	assertEquals(1, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("   ",it.next());
    	assertEquals(2, it.getLastReturnedLine());
    	assertEquals(false,it.hasNext());
    }
    
    public void testIterator6() throws Exception {
    	String str = "" +
    	"'''\n" +
    	"\n" +
    	"'''\n" +
    	"class Foo:\n" +
    	"    '''\n" +
    	"    \"\n"+
    	"    b\n"+
    	"    '''a\n"+
    	"    pass\n"+
    	"    def m1(self):\n" +
    	"        '''\n" +
    	"        eueueueueue\n" +
    	"        '''\n" +
    	"\n" +
    	"\n";
    	Document d = new Document(str);
    	PyDocIterator it = new PyDocIterator(d, false, true, true);
    	assertEquals(-1, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	for (int i = 0; i < d.getNumberOfLines()-1; i++) {
			it.next();
			assertEquals(i, it.getLastReturnedLine());
			if(i == d.getNumberOfLines()-2){
				assertTrue("Failed at line:"+i,!it.hasNext());
				
			}else{
				assertTrue("Failed at line:"+i,it.hasNext());
			}
		}
    }
    
    public void testIterator4() throws Exception {
    	String str = "" +
    	"pass\r" +
    	"foo\n" +
    	"bla\r\n" +
    	"what";
    	Document d = new Document(str);
    	PyDocIterator it = (PyDocIterator) ParsingUtils.getNoLiteralsOrCommentsIterator(d);
    	assertEquals(-1, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("pass\r",it.next());
    	assertEquals(0, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("foo\n",it.next());
    	assertEquals(1, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("bla\r\n",it.next());
    	assertEquals(2, it.getLastReturnedLine());
    	assertEquals(true,it.hasNext());
    	
    	assertEquals("what",it.next());
    	assertEquals(3, it.getLastReturnedLine());
    	assertEquals(false,it.hasNext());
    }
    
    
    

    public void testMakeParseable() throws Exception {
        assertEquals("a=1\r\n", ParsingUtils.makePythonParseable("a=1", "\r\n"));
        
        String code = 
            "class C:\n" +
            "    pass";
        String expected = 
            "class C:\r\n" +
            "    pass\r\n" +
            "\r\n";
        assertEquals(expected, ParsingUtils.makePythonParseable(code, "\r\n"));
        
        code = 
            "class C:" +
            "";
        expected = 
            "class C:\r\n" +
            "";
        assertEquals(expected, ParsingUtils.makePythonParseable(code, "\r\n"));
        
        code = 
            "    def m1(self):" +
            "";
        expected = 
            "    def m1(self):\r\n" +
            "";
        assertEquals(expected, ParsingUtils.makePythonParseable(code, "\r\n"));
        
        code = 
            "class C:\n" +
            "    pass\n" +
            "a = 10";
        expected = 
            "class C:\r\n" +
            "    pass\r\n" +
            "\r\n" +
            "a = 10" +
            "\r\n";
        assertEquals(expected, ParsingUtils.makePythonParseable(code, "\r\n"));
        
        code = 
            "class C:\n" +
            "    \n" +
            "    pass\n" +
            "a = 10";
        expected = 
            "class C:\r\n" +
            "    pass\r\n" +
            "\r\n" +
            "a = 10" +
            "\r\n";
        assertEquals(expected, ParsingUtils.makePythonParseable(code, "\r\n"));
        
        code = 
            "class AAA:\n" +
            "    \n" +
            "    \n" +
            "    def m1(self):\n" +
            "        self.bla = 10\n" +
            "\n" +
            "";
        expected = 
            "class AAA:\r\n" +
            "    def m1(self):\r\n" +
            "        self.bla = 10\r\n" +
            "\r\n";
        assertEquals(expected, ParsingUtils.makePythonParseable(code, "\r\n"));
        
        code = 
            "a=10"+
            "";
        expected = 
            "\na=10\n" +
            "";
        assertEquals(expected, ParsingUtils.makePythonParseable(code, "\n", new FastStringBuffer("    pass", 16)));
    }
    
}
