import React, {Component} from 'react';
import { FlatList, ActivityIndicator, Text, View, TextInput, TouchableOpacity } from 'react-native';
import {URL} from '../Login/const';
import {getAuth} from '../Login/auth';
import styles from '../Login/Design';
import {setState} from '../Login/state';
import {ProjectInfo} from '../Projects/projectInfo';
import {sendMessage, setMsg} from './sendMessages';
import {
	StackNavigator,
  } from 'react-navigation';

export default class GetMessages extends Component {

    static navigationOptions= {
		title: 'Chat',
		headerStyle: {
			backgroundColor:'#5daedb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
    } 

  constructor(props){
    super(props);
    this.state ={ isLoading: true, message: "", error: "", ticketID:[]
    }
  }

  async onSendPressed() {
   

   setMsg(this.state.message);
   //if (message === '') {
   // this.setState({error: "message empty"});
  // }
    sendMessage();
    this.makeApiCall();

}

async makeApiCall() {
  return fetch(URL + '/messages/1' , {method:'GET', headers: getAuth()})
  .then((response) => response.json())
  .then((responseJson) => {

    this.setState({
      isLoading: false,
      dataSource: responseJson,
    }, function(){

    });

  })
  .catch((error) =>{
    console.error(error);
  });

}

  componentDidMount(){
    this.makeApiCall();
  }



  render(){

    if(this.state.isLoading){
      return(
        <View style={{flex: 1, padding: 20}}>
          <ActivityIndicator/>
        </View>
      )
    }

    return(
      <View style={styles.container}>
        <FlatList
          data={this.state.dataSource}
          renderItem={({item}) => <Text style={styles.text}>{item.sender} : {item.content} </Text> }
          keyExtractor={(item, id) => id}
        />

        <TextInput onChangeText={(text) => this.setState({message: text})} placeholder="Message" underlineColorAndroid="transparent" style={styles.inputLong} />
            <TouchableOpacity
            onPress={this.onSendPressed.bind(this)} 
             style={styles.buttonLargeContainer}>
			
				<Text style={styles.buttonText}>SEND</Text>

			</TouchableOpacity>
            <Text style={styles.error}>
                    {this.state.error}
                   
				</Text>
      </View>
      
    );
  }
}
