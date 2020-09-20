package com.skaas.dynamodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

public class DynamoDBQuery {

	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
    static DynamoDB dynamoDB = new DynamoDB(client);

    static String tableName = "orders";
    
	public static void main(String[] args) {

		try {
			createTable();
			loadData();	
			queryWithTimeRange("timmy","2017-06-30T18:39:48.232Z","2017-08-18T18:39:48.232Z");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void createTable() throws InterruptedException {
		List<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("username").withAttributeType(ScalarAttributeType.S));
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("order_timestamp").withAttributeType(ScalarAttributeType.S));

        KeySchemaElement hashKey = new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName("username");
        KeySchemaElement rangeKey = new KeySchemaElement().withKeyType(KeyType.RANGE).withAttributeName("order_timestamp");
        
        List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
        keySchema.add(hashKey);
        keySchema.add(rangeKey);
		        
		CreateTableRequest request = new CreateTableRequest()
		        .withTableName(tableName)
		        .withKeySchema(keySchema)
		        .withAttributeDefinitions(attributeDefinitions)
		        .withProvisionedThroughput(
	        		new ProvisionedThroughput()
			            .withReadCapacityUnits(2L)
			            .withWriteCapacityUnits(2L)
	            );
		System.out.println("Creating table if doesn't exist");
		TableUtils.createTableIfNotExists(client, request);
        TableUtils.waitUntilActive(client, tableName);
	}

	private static void loadData() {
        Table table = dynamoDB.getTable(tableName);
        
        Item item1 = new Item().withPrimaryKey("username", "timmy").withString("order_timestamp", "2017-07-29T18:39:48.232Z").withNumber("amount", 500);
        Item item2 = new Item().withPrimaryKey("username", "jimmy").withString("order_timestamp", "2017-08-29T18:39:48.232Z").withNumber("amount", 250);
        Item item3 = new Item().withPrimaryKey("username", "harry").withString("order_timestamp", "2017-06-29T18:39:48.232Z").withNumber("amount", 130);
        Item item4 = new Item().withPrimaryKey("username", "timmy").withString("order_timestamp", "2017-07-15T18:39:48.232Z").withNumber("amount", 750);

        System.out.println("Loading data into table");
        table.putItem(item1);
        table.putItem(item2);
        table.putItem(item3);
        table.putItem(item4);
	}
	
	private static void queryWithTimeRange(String username, String endDate, String startDate) {
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("username = :user and order_timestamp between :end and :start")
                .withValueMap(new ValueMap().withString(":user", username).withString(":end", endDate).withString(":start", startDate));

        Table table = dynamoDB.getTable(tableName);
        ItemCollection<QueryOutcome> items = table.query(spec);

        System.out.println("\nItems from the query result:");
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().toJSONPretty());
        }
	}
}