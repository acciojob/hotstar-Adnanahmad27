package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    WebSeriesRepository webSeriesRepository;
    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User u =userRepository.save(user);

        return u.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Web series from the WebRepository
        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        SubscriptionType subscriptionType = subscription.getSubscriptionType();

        List<WebSeries> webSeriesList = webSeriesRepository.findAll();

//        int cntOfWeb = 0;
//        for(WebSeries webSeries : webSeriesList){
//            if(webSeries.getSubscriptionType().equals(subscriptionType) && webSeries.getAgeLimit()<=user.getAge()){
//                cntOfWeb++;
//            }
//        }
//        return cntOfWeb;

        int cntOfBasicWeb = 0;
        int cntOfProWeb = 0;
        int cntOfEliteWeb = 0;

        for(WebSeries webSeries : webSeriesList){
            if(webSeries.getSubscriptionType().equals(SubscriptionType.BASIC) && webSeries.getAgeLimit()<=user.getAge()){
                cntOfBasicWeb++;
            }
            if(webSeries.getSubscriptionType().equals(SubscriptionType.PRO) && webSeries.getAgeLimit()<=user.getAge()){
                cntOfProWeb++;
            }
            if(webSeries.getSubscriptionType().equals(SubscriptionType.ELITE) && webSeries.getAgeLimit()<=user.getAge()){
                cntOfEliteWeb++;
            }
        }
        if(subscriptionType.equals(SubscriptionType.BASIC)){
            return cntOfBasicWeb;
        }else if(subscriptionType.equals(SubscriptionType.PRO)){
            return cntOfBasicWeb+cntOfProWeb;
        }else{
            return cntOfBasicWeb+cntOfProWeb+cntOfEliteWeb;
        }
    }
}
