import React, {Component} from 'react';
import { StyleSheet, Text, View, ScrollView} from 'react-native';
import styles from '../Login/Design';
import {key} from './keyValid';
import {ticket} from '../Chat/sendMessages';
import {
	StackNavigator,
  } from 'react-navigation';


  export default class TicketDetails extends Component {

    static navigationOptions= {
		title: 'Ticket Details',
		headerStyle: {
			backgroundColor:'#5daedb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
    }

    constructor(props) {
        super(props);
        this.state = {
			isLoading: true,
            data: [],
           
		};
    }

    //componentDidMount() {
     //   fetch(URL + '/projects/' + key + '/tickets', {method:'GET', headers: getAuth()})
     //     .then((response) => response.json())
      //    .then((responseJson) => {
      //     this.setState({
        //      isLoading: false,
        //     data: responseJson
      //      }, function() {});
       //   }).catch((error) => {
        //     console.error(error);
       //  }); 
    //}
    
    render() {

      
        //if (this.state.isLoading) {
           //   return (
            //      <View style={{flex: 1,padding: 20}}>
             //         <ActivityIndicator/>
            //      </View>
            //  )
        //  }
       
         return (
          <View style={styles.container}>
                 <View style={{width:345,height:100, backgroundColor:'purple', padding:20}}>
                 
                 </View>
          </View>
          
        );
      }
    
  }

  