import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput} from 'react-native';
import styles from '../Login/Design';
import {username} from '../Login/auth';
import {GiftedChat} from 'react-native-gifted-chat';
import {
	StackNavigator,
  } from 'react-navigation';

export default class TicketChat extends Component {

  static navigationOptions= {
		title: 'Chat',
		headerStyle: {
			backgroundColor:'#8eacbb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
    } 

    state = {
        messages:[],
    }

    render() {
      
      return (
        <View style={styles.container}>
        
         
             <Text style={styles.buttonText}>Hello {username}</Text>
       
    
     </View>
      );
    }
  }
//        <GiftedChat
 //       messages={this.state.messages}
//       onSend={(message) => {
        //send to backend
  //      }}
  //      user={{
  //          _id:1,
        //getUserID    
   //     }

    //    }
    //    />
    //  );
   // }
//  }

 