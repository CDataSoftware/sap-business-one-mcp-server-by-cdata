# sap-business-one-mcp-server-by-cdata
CData's Model Context Protocol (MCP) Server for SAP Business One

:heavy_exclamation_mark: This project builds a read-only MCP server. For full read, write, update, delete, and action capabilities and a simplified setup, check out our free [CData MCP Server for SAP Business One (beta)](https://www.cdata.com/download/download.aspx?sku=FVZM-V&type=beta). 
## Purpose
We created this read-only MCP Server to allow LLMs (like Claude Desktop) to query live data SAP Business One supported by the [CData JDBC Driver for SAP Business One](https://www.cdata.com/drivers/sapbusinessone/jdbc).

CData JDBC Driver connects to SAP Business One by exposing them as relational SQL models.

This server wraps that driver and makes SAP Business One data available through a simple MCP interface, so LLMs can retrieve live information by asking natural language questions — no SQL required.

## Setup Guide
1. Clone the repository:
      ```bash
      git clone https://github.com/cdatasoftware/sap-business-one-mcp-server-by-cdata.git
      cd sap-business-one-mcp-server-by-cdata
      ```
2. Build the server:
      ```bash
      mvn clean install
      ``` 
      This creates the JAR file: CDataMCP-jar-with-dependencies.jar
2. Download and install the CData JDBC Driver for {source}: [https://www.cdata.com/drivers/sapbusinessone/download/jdbc](https://www.cdata.com/drivers/sapbusinessone/download/jdbc)
3. License the CData JDBC Driver:
    * Navigate to the `lib` folder in the installation directory, typically:
        * (Windows) `C:\Program Files\CData\CData JDBC Driver for SAP Business One\`
        * (Mac/Linux) `/Applications/CData JDBC Driver for SAP Business One/`
    * Run the command `java -jar cdata.jdbc.sapbusinessone.jar --license`
    * Enter your name, email, and "TRIAL" (or your license key).
4. Configure your connection to the data source (Salesforce as an example):
    * Run the command `java -jar cdata.jdbc.sapbusinessone.jar` to open the Connection String utility.
      
      <img src="https://github.com/user-attachments/assets/a5b5237b-79c1-472c-8c2f-3f9eb1ac9627" title="CData JDBC Driver Connectiong String utility." width=384px />
    * Configure the connection string and click "Test Connection"
      > **Note:** If the data sources uses OAuth, you will need to authenticate in your browser.
    * Once successful, copy the connection string for use later.
5. Create a `.prp` file for your JDBC connection (e.g. `sap-business-one.prp`) using the following properties and format:
    * **Prefix** - a prefix to be used for the tools exposed
    * **ServerName** - a name for your server
    * **ServerVersion** - a version for your server
    * **DriverPath** - the full path to the JAR file for your JDBC driver
    * **DriverClass** - the name of the JDBC Driver Class (e.g. cdata.jdbc.sapbusinessone.SAPBusinessOneDriver)
    * **JdbcUrl** - the JDBC connection string to use with the CData JDBC Driver to connect to your data (copied from above)
    * **Tables** - leave blank to access all data, otherwise you can explicitly declare the tables you wish to create access for
      ```env
      Prefix=sapbusinessone
      ServerName=CDataSAPBusinessOne
      ServerVersion=1.0
      DriverPath=PATH\TO\cdata.jdbc.sapbusinessone.jar
      DriverClass=cdata.jdbc.sapbusinessone.SAPBusinessOneDriver
      JdbcUrl=jdbc:sapbusinessone:InitiateOAuth=GETANDREFRESH;
      Tables=
      ```

## Using the Server with Claude Desktop
1. Create the config file for Claude Desktop ( claude_desktop_config.json) to add the new MCP server, using the format below. If the file already exists, add the entry to the `mcpServers` in the config file.

      **Windows**
      ```json
      {
        "mcpServers": {
          "{classname_dash}": {
            "command": "PATH\\TO\\java.exe",
            "args": [
              "-jar",
              "PATH\\TO\\CDataMCP-jar-with-dependencies.jar",
              "PATH\\TO\\sap-business-one.prp"
            ]
          },
          ...
        }
      }
      ```
      
      **Linux/Mac**
      ```json
      {
        "mcpServers": {
          "{classname_dash}": {
            "command": "/PATH/TO/java",
            "args": [
              "-jar",
              "/PATH/TO/CDataMCP-jar-with-dependencies.jar",
              "/PATH/TO/sap-business-one.prp"
            ]
          },
          ...
        }
      }
      ```
      If needed, copy the config file to the appropriate directory (Claude Desktop as the example).
      **Windows**
      ```bash
      cp C:\PATH\TO\claude_desktop_config.json %APPDATA%\Claude\claude_desktop_config.json
      ```
      **Linux/Mac**
      ```bash
      cp /PATH/TO/claude_desktop_config.json /Users/{user}/Library/Application\ Support/Claude/claude_desktop_config.json'
      ```
2. Run or refresh your client (Claude Desktop).
   
> **Note:** You may need to fully exit or quit your Claude Desktop client and re-open it for the MCP Servers to appear.

## Running the Server
1. Run the follow the command to run the MCP Server on its own
      ```bash
      java -jar /PATH/TO/CDataMCP-jar-with-dependencies.jar /PATH/TO/Salesforce.prp
> **Note:** The server uses `stdio` so can only be used with clients that run on the same machine as the server.
## Usage Details
Once the MCP Server is configured, the AI client will be able to use the built-in tools to read, write, update, and delete the underlying data. In general, you do not need to call the tools explicitly. Simply ask the client to answer questions about the underlying data system. For example:
* "What is the correlation between my closed won opportunities and the account industry?"
* "How many open tickets do I have in the SUPPORT project?"
* "Can you tell me what calendar events I have today?"

The list of tools available and their descriptions follow:
### Tools & Descriptions
In the definitions below, `{servername}` refers to the name of the MCP Server in the config file (e.g. `{classname_dash}` above).
* `{servername}_get_tables` - Retrieves a list of tables available in the data source. Use the `{servername}_get_columns` tool to list available columns on a table. The output of the tool will be returned in CSV format, with the first line containing column headers.
* `{servername}_get_columns` - Retrieves a list of columns for a table. Use the `{servername}_get_tables` tool to get a list of available tables. The output of the tool will be returned in CSV format, with the first line containing column headers.
* `{servername}_run_query` - Execute a SQL SELECT query

## JSON-RPC Request Examples
If you are scripting out the requests sent to the MCP Server instead of using an AI Client (e.g. Claude), then you can refer to the JSON payload examples below – following the JSON-RPC 2.0 specification - when calling the available tools. 

#### sap_business_one_get_tables
```json
{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "tools/call",
    "params": {
        "name": "sap_business_one_get_tables",
        "arguments": {}
    }
}
```

#### sap_business_one_get_columns
```json
{
    "jsonrpc": "2.0",
    "id": 2,
    "method": "tools/call",
    "params": {
        "name": "sap_business_one_get_columns",
        "arguments": {
            "table":  "Account"
        }
    }
}
```

#### sap_business_one_run_query
```json
{
    "jsonrpc": "2.0",
    "id": 3,
    "method": "tools/call",
    "params": {
        "name": "sap_business_one_run_query",
        "arguments": {
            "sql":  "SELECT * FROM [Account] WHERE [IsDeleted] = true"
        }
    }
}
```

## Troubleshooting
1. If you cannot see your CData MCP Server in Claude Desktop, be sure that you have fully quit Claude Desktop (Windows: use the Task Manager, Mac: use the Activity Monitor)
2. If Claude Desktop is unable to retrieve data, be sure that you have configured your connection properly. Use the Connection String builder to create the connection string (see above) and copy the connection string into the property (.prp) file.
3. If you are having trouble connecting to your data source, contact the [CData Support Team](https://www.cdata.com/support/submit.aspx).
4. If you are having trouble using the MCP server, or have any other feedback, join the [CData Community](https://community.cdata.com).

## License
This MCP server is licensed under the MIT License. This means you are free to use, modify, and distribute the software, subject to the terms and conditions of the MIT License. For more details, please see the [LICENSE](./LICENSE) file in the project repository.

## All Supported Sources
<table>
<tr><td>Access</td><td>Act CRM</td><td>Act-On</td><td>Active Directory</td></tr>
<tr><td>ActiveCampaign</td><td>Acumatica</td><td>Adobe Analytics</td><td>Adobe Commerce</td></tr>
<tr><td>ADP</td><td>Airtable</td><td>AlloyDB</td><td>Amazon Athena</td></tr>
<tr><td>Amazon DynamoDB</td><td>Amazon Marketplace</td><td>Amazon S3</td><td>Asana</td></tr>
<tr><td>Authorize.Net</td><td>Avalara AvaTax</td><td>Avro</td><td>Azure Active Directory</td></tr>
<tr><td>Azure Analysis Services</td><td>Azure Data Catalog</td><td>Azure Data Lake Storage</td><td>Azure DevOps</td></tr>
<tr><td>Azure Synapse</td><td>Azure Table</td><td>Basecamp</td><td>BigCommerce</td></tr>
<tr><td>BigQuery</td><td>Bing Ads</td><td>Bing Search</td><td>Bitbucket</td></tr>
<tr><td>Blackbaud FE NXT</td><td>Box</td><td>Bullhorn CRM</td><td>Cassandra</td></tr>
<tr><td>Certinia</td><td>Cloudant</td><td>CockroachDB</td><td>Confluence</td></tr>
<tr><td>Cosmos DB</td><td>Couchbase</td><td>CouchDB</td><td>CSV</td></tr>
<tr><td>Cvent</td><td>Databricks</td><td>DB2</td><td>DocuSign</td></tr>
<tr><td>Dropbox</td><td>Dynamics 365</td><td>Dynamics 365 Business Central</td><td>Dynamics CRM</td></tr>
<tr><td>Dynamics GP</td><td>Dynamics NAV</td><td>eBay</td><td>eBay Analytics</td></tr>
<tr><td>Elasticsearch</td><td>Email</td><td>EnterpriseDB</td><td>Epicor Kinetic</td></tr>
<tr><td>Exact Online</td><td>Excel</td><td>Excel Online</td><td>Facebook</td></tr>
<tr><td>Facebook Ads</td><td>FHIR</td><td>Freshdesk</td><td>FTP</td></tr>
<tr><td>GitHub</td><td>Gmail</td><td>Google Ad Manager</td><td>Google Ads</td></tr>
<tr><td>Google Analytics</td><td>Google Calendar</td><td>Google Campaign Manager 360</td><td>Google Cloud Storage</td></tr>
<tr><td>Google Contacts</td><td>Google Data Catalog</td><td>Google Directory</td><td>Google Drive</td></tr>
<tr><td>Google Search</td><td>Google Sheets</td><td>Google Spanner</td><td>GraphQL</td></tr>
<tr><td>Greenhouse</td><td>Greenplum</td><td>HarperDB</td><td>HBase</td></tr>
<tr><td>HCL Domino</td><td>HDFS</td><td>Highrise</td><td>Hive</td></tr>
<tr><td>HubDB</td><td>HubSpot</td><td>IBM Cloud Data Engine</td><td>IBM Cloud Object Storage</td></tr>
<tr><td>IBM Informix</td><td>Impala</td><td>Instagram</td><td>JDBC-ODBC Bridge</td></tr>
<tr><td>Jira</td><td>Jira Assets</td><td>Jira Service Management</td><td>JSON</td></tr>
<tr><td>Kafka</td><td>Kintone</td><td>LDAP</td><td>LinkedIn</td></tr>
<tr><td>LinkedIn Ads</td><td>MailChimp</td><td>MariaDB</td><td>Marketo</td></tr>
<tr><td>MarkLogic</td><td>Microsoft Dataverse</td><td>Microsoft Entra ID</td><td>Microsoft Exchange</td></tr>
<tr><td>Microsoft OneDrive</td><td>Microsoft Planner</td><td>Microsoft Project</td><td>Microsoft Teams</td></tr>
<tr><td>Monday.com</td><td>MongoDB</td><td>MYOB AccountRight</td><td>MySQL</td></tr>
<tr><td>nCino</td><td>Neo4J</td><td>NetSuite</td><td>OData</td></tr>
<tr><td>Odoo</td><td>Office 365</td><td>Okta</td><td>OneNote</td></tr>
<tr><td>Oracle</td><td>Oracle Eloqua</td><td>Oracle Financials Cloud</td><td>Oracle HCM Cloud</td></tr>
<tr><td>Oracle Sales</td><td>Oracle SCM</td><td>Oracle Service Cloud</td><td>Outreach.io</td></tr>
<tr><td>Parquet</td><td>Paylocity</td><td>PayPal</td><td>Phoenix</td></tr>
<tr><td>PingOne</td><td>Pinterest</td><td>Pipedrive</td><td>PostgreSQL</td></tr>
<tr><td>Power BI XMLA</td><td>Presto</td><td>Quickbase</td><td>QuickBooks</td></tr>
<tr><td>QuickBooks Online</td><td>QuickBooks Time</td><td>Raisers Edge NXT</td><td>Reckon</td></tr>
<tr><td>Reckon Accounts Hosted</td><td>Redis</td><td>Redshift</td><td>REST</td></tr>
<tr><td>RSS</td><td>Sage 200</td><td>Sage 300</td><td>Sage 50 UK</td></tr>
<tr><td>Sage Cloud Accounting</td><td>Sage Intacct</td><td>Salesforce</td><td>Salesforce Data Cloud</td></tr>
<tr><td>Salesforce Financial Service Cloud</td><td>Salesforce Marketing</td><td>Salesforce Marketing Cloud Account Engagement</td><td>Salesforce Pardot</td></tr>
<tr><td>Salesloft</td><td>SAP</td><td>SAP Ariba Procurement</td><td>SAP Ariba Source</td></tr>
<tr><td>SAP Business One</td><td>SAP BusinessObjects BI</td><td>SAP ByDesign</td><td>SAP Concur</td></tr>
<tr><td>SAP Fieldglass</td><td>SAP HANA</td><td>SAP HANA XS Advanced</td><td>SAP Hybris C4C</td></tr>
<tr><td>SAP Netweaver Gateway</td><td>SAP SuccessFactors</td><td>SAS Data Sets</td><td>SAS xpt</td></tr>
<tr><td>SendGrid</td><td>ServiceNow</td><td>SFTP</td><td>SharePoint</td></tr>
<tr><td>SharePoint Excel Services</td><td>ShipStation</td><td>Shopify</td><td>SingleStore</td></tr>
<tr><td>Slack</td><td>Smartsheet</td><td>Snapchat Ads</td><td>Snowflake</td></tr>
<tr><td>Spark</td><td>Splunk</td><td>SQL Analysis Services</td><td>SQL Server</td></tr>
<tr><td>Square</td><td>Stripe</td><td>Sugar CRM</td><td>SuiteCRM</td></tr>
<tr><td>SurveyMonkey</td><td>Sybase</td><td>Sybase IQ</td><td>Tableau CRM Analytics</td></tr>
<tr><td>Tally</td><td>TaxJar</td><td>Teradata</td><td>Tier1</td></tr>
<tr><td>TigerGraph</td><td>Trello</td><td>Trino</td><td>Twilio</td></tr>
<tr><td>Twitter</td><td>Twitter Ads</td><td>Veeva CRM</td><td>Veeva Vault</td></tr>
<tr><td>Wave Financial</td><td>WooCommerce</td><td>WordPress</td><td>Workday</td></tr>
<tr><td>xBase</td><td>Xero</td><td>XML</td><td>YouTube Analytics</td></tr>
<tr><td>Zendesk</td><td>Zoho Books</td><td>Zoho Creator</td><td>Zoho CRM</td></tr>
<tr><td>Zoho Inventory</td><td>Zoho Projects</td><td>Zuora</td><td>... Dozens More</td></tr>
</table>

