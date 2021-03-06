package smells;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import metrics.FileMetrics;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import results.Occurrence;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class LazyClassSmellTest {

    private static LazyClassSmell lcs;

    @Parameterized.Parameter(0)
    public static int expSeverity;

    @Parameterized.Parameter(1)
    public static int actSeverity;

    @Parameterized.Parameter(2)
    public static List<Occurrence> occurrences;

    @Parameterized.Parameters
    public static Collection<Object> data(){
        CompilationUnit[] CUs = new CompilationUnit[] {
                JavaParser.parse(
                        "public class ArrayQueue<T> implements Queue<T> {\n" +
                                "\n" +
                                "\tprivate T[] array;\n" +
                                "\tprivate int front = 0; //initial position of the front of the queue\n" +
                                "\tprivate int rear = 0; //initial position of the rear of the queue\n" +
                                "\t\n" +
                                "\t//Constructor without arguments creates an array of size 10\n" +
                                "\tpublic ArrayQueue(){\n" +
                                "\t\tthis(10);\n" +
                                "\t}\n" +
                                "\t\n" +
                                "\t//Constructor that takes in a size argument\n" +
                                "\t@SuppressWarnings(\"unchecked\")\n" +
                                "\tpublic ArrayQueue(int size) {\n" +
                                "\t\tarray = (T[]) new Object[size];\n" +
                                "\t}\n" +
                                "\t\n" +
                                "\t/**\n" +
                                "\t * Adds an element to the end of the queue and\n" +
                                "\t * it also resizes the array if the queue is about to be full\n" +
                                "\t */\n" +
                                "\t@Override\n" +
                                "\tpublic void enqueue(T object) {\n" +
                                "\t\tif(size() == array.length-1)\n" +
                                "\t\t\tresize();\n" +
                                "\t\tInsert test = new Insert(\"name\", new String[]{},new String[]{});\n" +
                                "\t\tarray[rear] = object;\n" +
                                "\t\trear = (rear+1) % array.length;\n" +
                                "\t}\n" +
                                "\n" +
                                "\t/**\n" +
                                "\t * Removing and returning the first element in the queue.\n" +
                                "\t * Throws a runtime exception is queue is empty\n" +
                                "\t * @return element at the end of the queue\n" +
                                "\t */\n" +
                                "\t@Override\n" +
                                "\tpublic T dequeue() {\n" +
                                "\t\tif(isEmpty()) throw new QueueEmptyException();\n" +
                                "\t\tT temp = array[front];\n" +
                                "\t\tarray[front] = null; //cleaning up the code by dereferencing the slot\n" +
                                "\t\tfront = (front+1) % array.length; //wrapping around the bounds for a circular array\n" +
                                "\t\treturn temp;\n" +
                                "\t}\n" +
                                "\n" +
                                "\t/**\n" +
                                "\t * Returns but does NOT remove the first element in the queue\n" +
                                "\t * @return element at the front of the queue\n" +
                                "\t */\n" +
                                "\t@Override\n" +
                                "\tpublic T front() {\n" +
                                "\t\tif(isEmpty()) throw new QueueEmptyException();\n" +
                                "\t\treturn array[front];\n" +
                                "\t}\n" +
                                "\n" +
                                "\t/**\n" +
                                "\t * @return true if the list is empty\n" +
                                "\t */\n" +
                                "\t@Override\n" +
                                "\tpublic boolean isEmpty() {\n" +
                                "\t\treturn front == rear;\n" +
                                "\t}\n" +
                                "\n" +
                                "\t/**\n" +
                                "\t * @return the number of elements in the queue\n" +
                                "\t */\n" +
                                "\t@Override\n" +
                                "\tpublic int size() {\n" +
                                "\t\treturn (array.length + rear - front) % array.length;\n" +
                                "\t}\n" +
                                "\t\n" +
                                "\t/**\n" +
                                "\t * Doubles the array size to allow for more elements in the queue\n" +
                                "\t */\n" +
                                "\t@SuppressWarnings(\"unchecked\")\n" +
                                "\tprivate void resize() {\n" +
                                "\t\tT[] newArray = (T[]) new Object[array.length * 2]; //the resized array\n" +
                                "\t\tint size = size();\n" +
                                "\t\tint i = 0;\n" +
                                "\t\twhile(!isEmpty()) {\n" +
                                "\t\t\tnewArray[i++] = dequeue();\n" +
                                "\t\t}\n" +
                                "\t\tfront = 0;\n" +
                                "\t\trear = size;\n" +
                                "\t\tarray = newArray;\n" +
                                "\t}\n" +
                                "\t\n" +
                                "\t/**\n" +
                                "\t * @return a string in the form: (queue size) / (current array size) : (elements of the queue from front to rear)\n" +
                                "\t */\n" +
                                "\tpublic String toString() {\n" +
                                "\t\tString queue = size() + \" / \" + array.length + \" : \";\n" +
                                "\t\tint i = front;\n" +
                                "\t\twhile(i != rear) {\n" +
                                "\t\t\tqueue += array[i] + \" \";\n" +
                                "\t\t\ti = (i+1)%array.length;\n" +
                                "\t\t}\n" +
                                "\t\treturn queue;\n" +
                                "\t}\n" +
                                "\n" +
                                "}"
                ),
                JavaParser.parse(
                        "public class Insert implements SQLStatement {\n" +
                                "\tprivate String name;\n" +
                                "\tprivate String[] columns;\n" +
                                "\tprivate String[] values;\n" +
                                "\t\n" +
                                "\tpublic Insert(String name, String[] columns, String[] values) {\n" +
                                "\t\tthis.name = name;\n" +
                                "\t\tthis.columns = columns;\n" +
                                "\t\tthis.values = values;\n" +
                                "\t}\n" +
                                "\t\n" +
                                "\t@Override\n" +
                                "\tpublic SQLResult execute(Map<String, Table> tables) throws SQLException {\n" +
                                "\t\tif (name.equals(\"mapsql.tables\")) throw new SQLException(\"Table 'mapsql.tables' cannot be modified\");\n" +
                                "\n" +
                                "\t\tfinal Table table = tables.get(name);\n" +
                                "\t\tif (table == null) throw new SQLException(\"Unknown table: \" + name);\n" +
                                "\n" +
                                "\t\ttable.description().checkForNotNulls(columns);\n" +
                                "\t\t\n" +
                                "\t\tString[] cols = table.description().resolveColumns(columns);\n" +
                                "\t\t\n" +
                                "\t\ttable.insert(cols, values);\n" +
                                "\t\treturn new SQLResult() {\n" +
                                "\n" +
                                "\t\t\t@Override\n" +
                                "\t\t\tpublic TableDescription description() {\n" +
                                "\t\t\t\treturn table.description();\n" +
                                "\t\t\t}\n" +
                                "\n" +
                                "\t\t\t@Override\n" +
                                "\t\t\tpublic List<Row> rows() {\n" +
                                "\t\t\t\treturn null;\n" +
                                "\t\t\t}\n" +
                                "\t\t\t\n" +
                                "\t\t\tpublic String toString() {\n" +
                                "\t\t\t\treturn \"OK\";\n" +
                                "\t\t\t}\n" +
                                "\t\t};\n" +
                                "\t}\n" +
                                "\n" +
                                "}"
                )
        };

        FileMetrics[] allMetrics = new FileMetrics[]{
                new FileMetrics(CUs[0]),
                new FileMetrics(CUs[1])
        };

        lcs = new LazyClassSmell(allMetrics);
        Object[][] data = new Object[2][];

        for(int i=0; i<2; i++){
            lcs.detectSmell(allMetrics[i]);
            data[i] = new Object[] {0, lcs.getSeverity(), lcs.getOccurrences()};
        }

        data[0][0] = 3;
        data[1][0] = 0;

        return Arrays.asList(data);
    }

    @Test
    public void getSeverity() {
        for(int i=0; i<4; i++){
            assertEquals(expSeverity, actSeverity);
        }
    }

    @Test
    public void getOccurrences() {
        assertNull(occurrences);
    }

    @Test
    public void getSmellName() {
        assertEquals("Lazy Class", lcs.getSmellName());
    }
}