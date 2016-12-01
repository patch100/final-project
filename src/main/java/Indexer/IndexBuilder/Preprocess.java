package Indexer.IndexBuilder;
import Indexer.Enums.CompressionLevel;
import Indexer.Models.Collection;
import Indexer.Models.Content;
import Indexer.Models.DocumentArticle;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static Scraper.MyCrawler.HTML_FOLDER;

public class Preprocess {
	public static void main(String[] args) {
		try {
			Collection collection = parseHTML();
			System.out.println("Generating unfiltered index...");
			Spimi.run(collection, CompressionLevel.UNFILTERED);
			Merger.merge("unfilteredIndex.txt");
			System.out.println("Generating numberless index...");
			Spimi.run(collection, CompressionLevel.NO_NUMBERS);
			Merger.merge("nonumbersIndex.txt");
			System.out.println("Generating case folded index...");
			Spimi.run(collection, CompressionLevel.CASE_FOLDING);
			Merger.merge("casefoldedIndex.txt");
			System.out.println("Generating 30 stop word index...");
			Spimi.run(collection, CompressionLevel.STOPW_30);
			Merger.merge("stopw30Index.txt");
			System.out.println("Generating 150 stop word index...");
			Spimi.run(collection, CompressionLevel.STOPW_150);
			Merger.merge("stopw150Index.txt");
			IndexStatistics.compareCompression();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Collection parseHTML() throws FileNotFoundException {
		System.out.println("Parsing html files...");
		List<DocumentArticle> allArticles = new ArrayList<DocumentArticle>();
		ArrayList<File> files = new ArrayList<File>();
		listOfFiles(HTML_FOLDER, files);
		int id = 1;
		//loop through all the html files
		for ( File file : files ) {
			FileReader fr = new FileReader(file);
			try {
				String text = ArticleExtractor.INSTANCE.getText(fr);
				String title = file.getName();
				System.out.println("adding " + title + " to collection with id: " + id);
				Content content = new Content(title, text);
				DocumentArticle doc = new DocumentArticle(id, content);
				id = id + 1;
				allArticles.add(doc);
			} catch (BoilerpipeProcessingException e) {
				e.printStackTrace();
			}
		}
		//return collection
		Collection col = new Collection();
		col.setDocuments(allArticles);
		return col;
	}

	private static void listOfFiles(String directoryName, ArrayList<File> files) {
		File directory = new File(directoryName);

		// get all the html files from a directory
		File[] fList = directory.listFiles();
		if (fList != null) {
			for (File file : fList) {
                if (file.isFile() && file.getPath().toLowerCase().endsWith(".html")) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    listOfFiles(file.getAbsolutePath(), files);
                }
            }
		}
	}
}