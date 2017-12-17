package stockapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import controller.DatabaseConnect;

@ManagedBean
@SessionScoped
public class StockApiBean{

    private static final long serialVersionUID = 1L;
    static final String API_KEY = "8LTIDPMBLJOF6JPZ";

    private String symbol;
    private String stock_symbol;
    private double price;
    private int qty;
    private double amt;
    private String table1Markup;
    private String table2Markup;
    private String purchased;
    public String getPurchased() {
		return purchased;
	}



	public void setPurchased(String purchased) {
		this.purchased = purchased;
	}

	private Float db_price;
    private Float db_amt;
    private Integer db_qty;
    private Integer temp_qty;

    private String selectedSymbol;
    private List<SelectItem> availableSymbols;

    public String getStock_symbol() {
		return stock_symbol;
	}


    public Float getDb_price() {
		return db_price;
	}



	public void setDb_price(Float db_price) {
		this.db_price = db_price;
	}



	public Float getDb_amt() {
		return db_amt;
	}



	public void setDb_amt(Float db_amt) {
		this.db_amt = db_amt;
	}



	public Integer getDb_qty() {
		return db_qty;
	}



	public void setDb_qty(Integer db_qty) {
		this.db_qty = db_qty;
	}




	public void setStock_symbol(String stock_symbol) {
		this.stock_symbol = stock_symbol;
	}

	public String getPurchaseSymbol() {
        if (getRequestParameter("symbol") != null) {
            symbol = getRequestParameter("symbol");
        }
        return symbol;
    }
	public String getSellSymbol() {
        if (getRequestParameter("symbol") != null) {
            symbol = getRequestParameter("symbol");
        }
        return symbol;
    }
    
    public void setPurchaseSymbol(String purchaseSymbol) {
        //System.out.println("func setPurchaseSymbol()");  //check
    }

    public double getPurchasePrice() {
        if (getRequestParameter("price") != null) {
            price = Double.parseDouble(getRequestParameter("price"));
            System.out.println("price: " + price);
        }
        return price;
    }
    public double getSellPrice() {
        if (getRequestParameter("price") != null) {
            price = Double.parseDouble(getRequestParameter("price"));
            System.out.println("price: " + price);
        }
        return price;
    }

    public void setPurchasePrice(double purchasePrice) {
        //System.out.println("func setPurchasePrice()");  //check
    }
    
    private String getRequestParameter(String name) {
        return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter(name);
    }
    private String selectedInterval;
    private List<SelectItem> availableIntervals;

    public String getSelectedInterval() {
        return selectedInterval;
    }

    public void setSelectedInterval(String selectedInterval) {
        this.selectedInterval = selectedInterval;
    }

    public List<SelectItem> getAvailableIntervals() {
        return availableIntervals;
    }

    public void setAvailableIntervals(List<SelectItem> availableIntervals) {
        this.availableIntervals = availableIntervals;
    }

    public String getSelectedSymbol() {
        return selectedSymbol;
    }

    public void setSelectedSymbol(String selectedSymbol) {
        this.selectedSymbol = selectedSymbol;
    }

    public List<SelectItem> getAvailableSymbols() {
        return availableSymbols;
    }

    public void setAvailableSymbols(List<SelectItem> availableSymbols) {
        this.availableSymbols = availableSymbols;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getAmt() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }

    public String getTable1Markup() {
        return table1Markup;
    }

    public void setTable1Markup(String table1Markup) {
        this.table1Markup = table1Markup;
    }

    public String getTable2Markup() {
        return table2Markup;
    }

    public void setTable2Markup(String table2Markup) {
        this.table2Markup = table2Markup;
    }

    @PostConstruct
    public void init() {
        //initially populate stock list
        availableSymbols = new ArrayList<SelectItem>();
        
        
        
        availableSymbols.add(new SelectItem("AAPL", "Apple"));
        availableSymbols.add(new SelectItem("AMZN", "Amazon LLC"));
        availableSymbols.add(new SelectItem("AR", "Antero Resources"));
        availableSymbols.add(new SelectItem("EBAY", "Ebay"));
        availableSymbols.add(new SelectItem("FB", "Facebook, Inc."));
        availableSymbols.add(new SelectItem("GOLD", "Gold"));
        availableSymbols.add(new SelectItem("GOOGL", "Google"));
        availableSymbols.add(new SelectItem("MSFT", "Microsoft"));
        availableSymbols.add(new SelectItem("SLV", "Silver"));
        availableSymbols.add(new SelectItem("TWTR", "Twitter, Inc."));

        //initially populate intervals for stock api
        availableIntervals = new ArrayList<SelectItem>();
        availableIntervals.add(new SelectItem("1min", "1min"));
        availableIntervals.add(new SelectItem("5min", "5min"));
        availableIntervals.add(new SelectItem("15min", "15min"));
        availableIntervals.add(new SelectItem("30min", "30min"));
        availableIntervals.add(new SelectItem("60min", "60min"));
    }

   
    public String createDbRecord(String symbol, double price, int qty, double amt) {
        try {
           
        	this.purchased="";
            Connection conn = DatabaseConnect.getConnection();
            Statement statement = conn.createStatement();
            ResultSet rs = null;
            PreparedStatement ps = null;
            
            System.out.println("Testing 1");
            Integer uid = Integer.parseInt((String) FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap().get("uid"));
            Double bal = Double.parseDouble((String) FacesContext.getCurrentInstance()
                            .getExternalContext()
                            .getSessionMap().get("bal"));
            
            System.out.println(uid);
            conn = DatabaseConnect.getConnection();
            ps = conn.prepareStatement("select stock_symbol,qty,amt from purchase where uid = '"+ uid + "'");
            rs = ps.executeQuery();
            if(rs.next())
            {
	            stock_symbol = rs.getString("stock_symbol");
	            System.out.println(stock_symbol);
	            db_qty=rs.getInt("qty");
	            temp_qty = db_qty;
	            db_amt = rs.getFloat("amt");
	            System.out.println(stock_symbol);
	            System.out.println(qty);
	            System.out.println(amt);
	            if(symbol.equals(stock_symbol))
	            {	
	            	qty = qty+db_qty;
	            	System.out.println(qty);
	            	amt = Math.round((amt + db_amt)*100.0)/100.0;
	            	
	            	System.out.println(amt);
	            	bal = bal - amt;
	            	statement.executeUpdate("UPDATE `purchase`  SET price = '" + price + "', qty = '" + qty + "', amt = '" + amt + "' WHERE uid = '" + uid + "'");
	            	statement.executeUpdate("UPDATE `users`  SET balance = '" + bal + "' WHERE uid = '" + uid + "'");
	            	statement.close();
	                conn.close();
	                this.purchased+="purchased";
	                FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully Updated stock",""));
	                
	            }
	            else {
	            	bal = bal - amt;
	            	statement.executeUpdate("INSERT INTO `purchase` (`id`, `uid`, `stock_symbol`, `qty`, `price`, `amt`) "
	                        + "VALUES (NULL,'" + uid + "','" + symbol + "','" + qty + "','" + price + "','" + amt +"')");
	            	statement.executeUpdate("UPDATE `users`  SET balance = '" + bal + "' WHERE uid = '" + uid + "'");
	            	statement.close();
	                conn.close();
	                this.purchased+="purchased";
	                FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully purchased stock",""));
	            }
                  
            }
            else
            {
            	bal = bal - amt;
            	statement.executeUpdate("INSERT INTO `purchase` (`id`, `uid`, `stock_symbol`, `qty`, `price`, `amt`) "
                        + "VALUES (NULL,'" + uid + "','" + symbol + "','" + qty + "','" + price + "','" + amt +"')");
            	statement.executeUpdate("UPDATE `users`  SET balance = '" + bal + "' WHERE uid = '" + uid + "'");
            	statement.close();
                conn.close();
                this.purchased+="purchased";
                FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully purchased stock",""));
            }
            
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "purchase";
    }

	public void installAllTrustingManager() {
        TrustManager[] trustAllCerts;
        trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println("Exception :" + e);
        }
        return;
    }
	
	
	public String sellRecord(String symbol, double price, int qty, double amt) {
        try {
           System.out.println("Price");
            Connection conn = DatabaseConnect.getConnection();
            Statement statement = conn.createStatement();
            ResultSet rs = null;
            PreparedStatement ps = null;
            
            
            Integer uid = Integer.parseInt((String) FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap().get("uid"));
            Double bal = Double.parseDouble((String) FacesContext.getCurrentInstance()
                            .getExternalContext()
                            .getSessionMap().get("bal"));
            
            System.out.println("Inside sell record "+uid);
            conn = DatabaseConnect.getConnection();
           	System.out.println(bal);
           		bal = bal + amt;
               	System.out.println(bal);
            	statement.executeUpdate("UPDATE `purchase`  SET qty = '" + qty + "' amt = '" + amt + "' WHERE uid = '" + uid + "'");
            	statement.executeUpdate("UPDATE `users`  SET balance = '" + bal + "' WHERE uid = '" + uid + "'");
            	statement.close();
                conn.close();
                FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully Sold stock",""));
            
                  
            
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "sell";
    }


    public void timeseries() throws MalformedURLException, IOException {

        installAllTrustingManager();
        Integer uid = Integer.parseInt((String) FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap().get("uid"));
       
        //System.out.println("selectedItem: " + this.selectedSymbol);
        //System.out.println("selectedInterval: " + this.selectedInterval);
        String symbol = this.selectedSymbol;
        String interval = this.selectedInterval;
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + symbol + "&interval=" + interval + "&apikey=" + API_KEY;
        System.out.println(symbol);
        this.table1Markup += "URL::: <a href='" + url + "'>Data Link</a><br>";
        InputStream inputStream = new URL(url).openStream();

        // convert the json string back to object
        JsonReader jsonReader = Json.createReader(inputStream);
        JsonObject mainJsonObj = jsonReader.readObject();
        
        for (String key : mainJsonObj.keySet()) {
            if (key.equals("Meta Data")) {
                this.table1Markup = ""; // reset table 1 markup before repopulating
                JsonObject jsob = (JsonObject) mainJsonObj.get(key);
                this.table1Markup += "<style>#detail >tbody > tr > td{ text-align:center;}</style><b>Stock Details</b>:<br>";
                this.table1Markup += "<table class='table-striped table-bordered'>";
                this.table1Markup += "<tr><td>Information</td><td>" + jsob.getString("1. Information") + "</td></tr>";
                this.table1Markup += "<tr><td>Symbol</td><td>" + jsob.getString("2. Symbol") + "</td></tr>";
                this.table1Markup += "<tr><td>Last Refreshed</td><td>" + jsob.getString("3. Last Refreshed") + "</td></tr>";
                this.table1Markup += "<tr><td>Interval</td><td>" + jsob.getString("4. Interval") + "</td></tr>";
                this.table1Markup += "<tr><td>Output Size</td><td>" + jsob.getString("5. Output Size") + "</td></tr>";
                this.table1Markup += "<tr><td>Time Zone</td><td>" + jsob.getString("6. Time Zone") + "</td></tr>";
                this.table1Markup += "</table>";
            } else {
                this.table2Markup = ""; // reset table 2 markup before repopulating
                JsonObject dataJsonObj = mainJsonObj.getJsonObject(key);
                this.table2Markup += "<table class='table table-striped table-bordered'>";
                this.table2Markup += "<thead><tr><th>Timestamp</th><th>Open</th><th>High</th><th>Low</th><th>Close</th><th>Volume</th></tr></thead>";
                this.table2Markup += "<tbody>";
                int i = 0;
                for (String subKey : dataJsonObj.keySet()) {
                    JsonObject subJsonObj = dataJsonObj.getJsonObject(subKey);
                    this.table2Markup
                            += "<tr>"
                            + "<td>" + subKey + "</td>"
                            + "<td>" + subJsonObj.getString("1. open") + "</td>"
                            + "<td>" + subJsonObj.getString("2. high") + "</td>"
                            + "<td>" + subJsonObj.getString("3. low") + "</td>"
                            + "<td>" + subJsonObj.getString("4. close") + "</td>"
                            + "<td>" + subJsonObj.getString("5. volume") + "</td>";
                    if (i == 0) {
                        String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
                        try {
                        	Connection conn = DatabaseConnect.getConnection();
                            Statement statement = conn.createStatement();
                            ResultSet rs = null;
                            PreparedStatement ps = null;
                            ps = conn.prepareStatement("select stock_symbol,qty,amt from purchase where uid = '"+ uid + "'");
                            rs = ps.executeQuery();
                           if(rs.next()) {
                        	   stock_symbol = rs.getString("stock_symbol");
                               System.out.println("Pranav is "+stock_symbol);
                               db_qty = rs.getInt("qty");
                               db_amt = rs.getFloat("amt");
                               this.table2Markup += "<td><a class='btn btn-success' href='" + path + "/purchase.xhtml?symbol=" + symbol + "&price=" + subJsonObj.getString("4. close") + "'>Buy Stock</a></td>";
                               System.out.println("this is "+ (stock_symbol));
                               System.out.println(symbol);
                               if(symbol.equals(stock_symbol))
                               {                               	
                               	this.table2Markup += "<td><a class='btn btn-success' href='" + path + "/sell.xhtml?symbol=" + symbol + "&price=" + subJsonObj.getString("4. close") + "&qty=" + db_qty +  "&amt=" + db_amt + "'>Sell Stock</a></td>";
                               }
                           }
                           else
                           {
                        	   this.table2Markup += "<td><a class='btn btn-success' href='" + path + "/purchase.xhtml?symbol=" + symbol + "&price=" + subJsonObj.getString("4. close") + "'>Buy Stock</a></td>";
                           }
                            
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }
                        
                    }
                    this.table2Markup += "</tr>";
                    i++;
                }
                this.table2Markup += "</tbody></table>";
            }
        }
        return;
    }
    
    
    
    
    
    public void purchaseStock() {
        System.out.println("Calling function purchaseStock()");
        System.out.println("stockSymbol: " + FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("stockSymbol"));
        System.out.println("stockPrice" + FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("stockPrice"));
        return;
    }
    
}
