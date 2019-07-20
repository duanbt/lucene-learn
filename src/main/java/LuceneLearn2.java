import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

/**
 * @author duanbt
 **/
public class LuceneLearn2 {

    private static IndexWriter indexWriter;

    static {
        try {
            URL url = LuceneLearn2.class.getClassLoader().getResource("");
            Path path = new File(url.getFile(), "index2").toPath();
            Directory directory = FSDirectory.open(path);

            //2. IndexWriter, 同时指定分词器
            IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer());
            indexWriter = new IndexWriter(directory, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addDoc() throws Exception {
        Document document = new Document();
        document.add(new TextField("fileName", "文件名", Field.Store.YES));
        document.add(new TextField("fileContent", "文件内容", Field.Store.NO));
        document.add(new StoredField("filePath", "c:/test"));
        indexWriter.addDocument(document);
        indexWriter.commit();
    }

    public static void deleteAllDoc() throws IOException {
        indexWriter.deleteAll();
        indexWriter.commit();
    }

    public static void deleteDocByQuery() throws IOException {
        indexWriter.deleteDocuments(new Term("fileName", "文件"));
        indexWriter.commit();
    }

    /**
     * 更新实际上是按条件查到后，先删除，再添加
     */
    public static void updateDoc() throws IOException {
        Document document = new Document();
        document.add(new TextField("fileName", "文件名修改", Field.Store.YES));
        document.add(new TextField("fileContent", "文件内容修改", Field.Store.NO));
        document.add(new StoredField("filePath", "c:/test"));
        indexWriter.updateDocument(new Term("fileName", "文件"), document);
        indexWriter.commit();
    }

    public static void main(String[] args) throws Exception {
//        addDoc();
//        deleteAllDoc();
//        deleteDocByQuery();
        updateDoc();
        indexWriter.close();
    }

}
