package com.sarthak.aws.dynamodb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    DynamoDBMapper dynamoDBMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AWSMobileClient.getInstance().initialize(this).execute();

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        addData();
        readData();
        queryData();
    }

    private void addData() {

        final Users userItem = new Users();

        userItem.setId("01");
        userItem.setName("ABC");
        userItem.setPhone("123456");
        userItem.setProfileImage("Image.com");

        new Thread(new Runnable() {
            @Override
            public void run() {

                dynamoDBMapper.save(userItem);
            }
        }).start();
    }

    private void readData() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Users users = dynamoDBMapper.load(Users.class, "01");
                Log.d("AMPLE", users.getPhone());
            }
        }).start();
    }

    private void queryData() {

        final Users userItem = new Users();

        userItem.setName("ABC");

        new Thread(new Runnable() {
            @Override
            public void run() {

                DynamoDBQueryExpression<Users> queryExpression = new DynamoDBQueryExpression<>();
                queryExpression.setHashKeyValues(userItem);
                queryExpression.setIndexName("name-index");
                queryExpression.setConsistentRead(false);

                List<Users> usersList = dynamoDBMapper.query(Users.class, queryExpression);

                for (int i = 0 ; i < usersList.size() ; i++) {

                    Log.d("Ample1", usersList.get(i).getName());
                }
            }
        }).start();
    }
}
