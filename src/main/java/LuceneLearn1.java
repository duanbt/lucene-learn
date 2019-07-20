import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
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
public class LuceneLearn1 {

    public static void createIndex() throws IOException {
        //1. 创建Directory
        URL url = LuceneLearn1.class.getClassLoader().getResource("");
        Path path = new File(url.getFile(), "index1").toPath();
        Directory directory = FSDirectory.open(path);

        //2. IndexWriter, 同时指定分词器
        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, config);

        //3. 读取文件创建Document，写入index
        File searchSourceDir = new File(LuceneLearn1.class.getClassLoader().getResource("searchsource").getFile());
        for (File file : searchSourceDir.listFiles()) {
            String fileName = file.getName();
            String filePath = file.getPath();
            String fileContent = FileUtils.readFileToString(file, "UTF-8");
            long fileSize = FileUtils.sizeOf(file);

            //创建Field
            Field fieldFileName = new TextField("fileName", fileName, Field.Store.YES);
            Field fieldFilePath = new StoredField("filePath", filePath);
            Field fieldFileContent = new TextField("fileContent", fileContent, Field.Store.YES);
            Field fieldFileSize = new LongPoint("fileSize", fileSize);
            Field fieldFileSizeStore = new StoredField("fileSize", fileSize);

            //创建Document
            Document document = new Document();
            document.add(fieldFileName);
            document.add(fieldFilePath);
            document.add(fieldFileContent);
            document.add(fieldFileSize);
            document.add(fieldFileSizeStore);

            //写入index
            indexWriter.addDocument(document);
        }
        indexWriter.close();
    }

    public static void searchIndex() throws IOException {
        //1. 创建Directory
        URL url = LuceneLearn1.class.getClassLoader().getResource("");
        Path path = new File(url.getFile(), "index").toPath();
        Directory directory = FSDirectory.open(path);

        //2. 创建IndexReader
        IndexReader indexReader = DirectoryReader.open(directory);

        //3. IndexSearcher
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        //4. Query
        Query query = new TermQuery(new Term("fileName", "spring"));

        //5. search
        TopDocs topDocs = indexSearcher.search(query, 10);

        System.out.println("查询 doc 总数: " + topDocs.totalHits);
        for (ScoreDoc doc : topDocs.scoreDocs) {
            int docId = doc.doc;
            Document document = indexSearcher.doc(docId);
            for (IndexableField indexableField : document) {
                System.out.println(indexableField);
            }
            System.out.println("=========================================================================");
        }

        indexReader.close();
    }

    public static void analyzer() throws IOException {
        //1. 创建Analyzer
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer(false);
        //2. 获得tokenStream
        TokenStream tokenStream = analyzer.tokenStream("test", "Lucene是一款高性能的、可扩展的信息检索(IR)工具库。信息检索是指文档搜索、文档内信息搜索或者文档相关的元数据搜索等操作。");
        //3. 添加引用
        //可获得每个关键词，
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //可获得关键词起始位置
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            System.out.println("start: " + offsetAttribute.startOffset());
            System.out.println(charTermAttribute);
            System.out.println("end: " + offsetAttribute.endOffset());
        }
        tokenStream.close();
    }


    public static void main(String[] args) throws IOException {
        analyzer();
    }
}
