import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, ActivityIndicator, FlatList} from 'react-native';
import styles from '../Login/Design';
import {
	StackNavigator,
  } from 'react-navigation';
import {getAuth,username,psw} from '../Login/auth';
import {URL} from '../Login/const';
import {key} from './keyValid';

window.btoa = require('Base64').btoa;


export default class ProjectInfo extends Component {

  constructor(props) {
		super(props);
		this.state = {
			isLoading: true,
            ticketList: [],
		};
	}
	
	
  componentDidMount() {

            fetch(URL + '/projects/' + key + '/tickets', {method:'GET', headers: getAuth()})
              .then((response) => response.json())
              .then((responseJson) => {
                this.setState({
                    isLoading: false,
                    ticketList: responseJson
                }, function() {});
              }).catch((error) => {
                console.error(error);
              }); 
          }
		
  
    static navigationOptions= {
		title: 'Tickets',
		headerStyle: {
			backgroundColor:'#5daedb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
    } 


    render() {
      var {params} = this.props.navigation.state;
      
      if (this.state.isLoading) {
			return (
				<View style={{flex: 1,padding: 20}}>
					<ActivityIndicator/>
				</View>
			)
		}
     
      return (
        <View style={styles.container}>
                <FlatList
                  style={styles.textLarge}
                  data={this.state.ticketList}
                  renderItem={({item}) => <TouchableOpacity
                 onPress={() =>{const { navigate } = this.props.navigation;
                 navigate("Sixth", { name: "GetMessage" })} }
                   style={styles.buttonContainer}>
                   <Text style={styles.buttonText}>
            {item.id}, {item.ticketSummary}, {item.ticketCategory}
            </Text>
            </TouchableOpacity>}
                  keyExtractor={(item, index) => index}
                />
        </View>
      );
    }
  }