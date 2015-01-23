import java.lang.Thread;
import java.util.regex.*;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.net.*;

import org.jsoup.Jsoup;
import org.jsoup.select.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;

class ProductExtractor implements Runnable
{
	private Thread t;
	private String threadURL;
	private String filename;

	ProductExtractor(String url, String fname)
	{
		threadURL = url;
		filename = fname;
	}

	public void run()
	{
		Document productPage = getHTML(threadURL);
		append(filename, extractProductID(threadURL) + ", "
			+ extractProductName(productPage) + ", "
			+ extractProductPrice(productPage)
			+ System.lineSeparator());
	}

	public void start()
	{
		if (t == null)
		{
			t = new Thread(this, threadURL);
			t.start();
		}
	}

	public static Document getHTML(String url)
	{
		URL obj;
		HttpURLConnection conn;
		int responseCode;
		BufferedReader inBuff;
		String inputLine;
		StringBuffer response;

		try
		{
			obj = new URL(url);
			conn = (HttpURLConnection)obj.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			responseCode = conn.getResponseCode();
			System.out.println("GET " + url + System.lineSeparator());

			inBuff = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	
			response = new StringBuffer();

			while ((inputLine = inBuff.readLine()) != null)
			{
				response.append(inputLine);
			}
			inBuff.close();
		}
		catch (MalformedURLException mal)
		{
			mal.printStackTrace();
			return null;
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			return null;
		}

		Document html = Jsoup.parse(response.toString());
		return html;
	}

	private static String extractProductID(String url)
	{
		Pattern productIdPattern = Pattern.compile("id=(\\d+)");
		Matcher productIdMatcher = productIdPattern.matcher(url);
		if (productIdMatcher.find())
		{
			return productIdMatcher.group().substring(3);
		}
		else
		{
			return null;
		}
	}

	private static String extractProductName(Document html)
	{
		return html.select("#productWrapper h1").text();
	}

	private String extractProductPrice(Document html)
	{
		String ppi = html.select("div.content.addToBasket p.price span.linePrice").first().text();
		String ppu = html.select("div.content.addToBasket p.price span.linePriceAbbr").first().text();
		return ppi + " " + ppu;
	}

	public static synchronized void append(String sFileName, String sContent)
	{
        try
        {
            File oFile = new File(sFileName);
            if (!oFile.exists())
            {
                oFile.createNewFile();
            }
            if (oFile.canWrite())
            {
                BufferedWriter oWriter = new BufferedWriter(new FileWriter(sFileName, true));
                oWriter.write(sContent);
                oWriter.close();
            }
        }
        catch (IOException ioe)
        {
        	ioe.printStackTrace();
        }
    }
}

public class Scraper
{
	public static void main(String args[])
	{
		String testURL = "http://www.tesco.com/groceries/product/browse/default.aspx?N=4294793220&Ne=4294793660";
		String fruitURL = "http://www.tesco.com/groceries/product/browse/default.aspx?N=4294793658&Ne=4294793660&lvl=3&action=toggleProdListSize_7";

		Document departmentHTML = ProductExtractor.getHTML(fruitURL);
		List<String> productsURLs = listProductURL(departmentHTML);
		for (String url : productsURLs)
		{
			ProductExtractor prodEx = new ProductExtractor("http://www.tesco.com" + url, args[0]);
			prodEx.start();
		}
	}

	private static List<String> listProductURL(Document html)
	{
		List<String> urls = new ArrayList<String>();
		Elements productsGrid = html.select("div.productLists ul.products.grid li");
		for (Element product : productsGrid)
		{
			urls.add(product.select("h2 a").attr("href"));
		}
		if (urls.get(urls.size() - 1) == "")
			urls = urls.subList(0, urls.size() - 1);
		return urls;
	}
}