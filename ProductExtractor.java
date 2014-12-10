import java.lang.Thread;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.net.*;

import org.jsoup.Jsoup;
import org.jsoup.select.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;

class ProductExtractor extends Thread
{
	public String url;

	public static void main(String args[])
	{
		Document productsGridPage = ProductExtractor.getDepHTML(args[0]);
		List<String> productsURLs = ProductExtractor.listProductURL(productsGridPage);

		for (String url : productsURLs)
		{
			Thread prodEx = new ProductExtractor(url);
			prodEx.start();
		}
	}

	public ProductExtractor(String url)
	{
		this.url  = url;
	}
	public void run()
	{
		Document productPage = this.getHTML("http://www.tesco.com" + this.url);
		String completeProductDescription = "";
		completeProductDescription += this.extractProductName(productPage) + " ";
		completeProductDescription += this.extractProductPrice(productPage) + "\n";
		completeProductDescription += this.extractNutritionInfo(productPage) + "\n";
		this.printResult(completeProductDescription);
	}

	public synchronized void printResult(String txt)
	{
		System.out.println(txt);
	}


	/*public void run()
	{
		Document productPage = prodEx.getHTML("http://www.tesco.com" + url);
		String completeProductDescription = "";
		completeProductDescription += prodEx.extractProductName(productPage) + " ";
		completeProductDescription += prodEx.extractProductPrice(productPage) + "\n";
		completeProductDescription += prodEx.extractNutritionInfo(productPage) + "\n\n";
		prodEx.writeTo(file, completeProductDescription);
	}

	public static void main(String[] args)
	{
		ProductExtractor prodEx = new ProductExtractor();

		Document productsGridPage = prodEx.getHTML(args[1]);
		List<String> productsURLs = prodEx.listProductURL(productsGridPage);

		try
		{
			File file = new File(args[0]);
	 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}			

			for (String url : productsURLs)
			{
				Thread t = new ProductExtractor();
				t.start();
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		System.out.println("Done!");
	}

	public synchronized void writeTo(File f, String productDescr)
	{
		try
		{
			FileWriter fw = new FileWriter(f.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(productDescr);
			bw.close();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}*/

	private String extractProductName(Document html)
	{
		return html.select("#productWrapper h1").text();
	}

	private List<String> extractNutritionInfo(Document html)
	{
		List<String> nutriValues = new ArrayList<String>();
		Elements nutriTable = html.select("table tbody tr");
		for (Element row : nutriTable)
		{
			nutriValues.add(row.select("td").first().text());
		}
		return nutriValues;
	}

	private String extractProductPrice(Document html)
	{
		String ppi = html.select("div.content.addToBasket p.price span.linePrice").text();
		String ppu = html.select("div.content.addToBasket p.price span.linePriceAbbr").text();
		return ppi + " " + ppu;
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

	public Document getHTML(String url)
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
			return null;
		}
		catch (IOException ioe)
		{
			return null;
		}


		Document html = Jsoup.parse(response.toString());
		return html;
	}

	public static Document getDepHTML(String url)
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
			return null;
		}
		catch (IOException ioe)
		{
			return null;
		}


		Document html = Jsoup.parse(response.toString());
		return html;
	}
}