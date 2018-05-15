import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, ActivityIndicator, FlatList} from 'react-native';
import styles from '../Login/Design';
import {
	StackNavigator,
  } from 'react-navigation';
import {getAuth} from '../Login/auth';
import {URL} from '../Login/const';
import {key} from './keyValid';

export default class ProjectInfo extends Component {

  constructor(props) {
		super(props);
		this.state = {
			isLoading: true
		}
	}
  
  componentDidMount() {
		var url = URL;
        var name = 'testproject';
		url += '/projects/' + name + '/tickets';
		return fetch(url, {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isLoading: false,
				dataSource: responseJson
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
	}
  
  static navigationOptions= {
		title: 'Project Info',
		headerStyle: {
			backgroundColor:'#8eacbb'
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
                  data={this.state.dataSource}
                  renderItem={({item}) => <Text>{item.id}, {item.ticketSummary}, {item.ticketCategory}</Text>}
                  keyExtractor={(item, index) => index}
                />
        </View>
      );
    }
  }
