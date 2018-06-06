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

export var ticketstatus = '';

export default class ProjectInfo extends Component {

  constructor(props) {
		super(props);
		this.state = {
			isLoading: true,
            tickets: [],
						stats: [],
						ticketsWithStats: [],
		};
	}

  componentDidMount() {
        fetch(URL + '/projects/' + key + '/tickets', {method:'GET', headers: getAuth()}
				)
        .then((response) => response.json())
          .then((responseJson) => {
            this.setState({
              tickets: responseJson
            }, function() {});
						for (var i=0; i < this.state.tickets.length; i++) {
							this.fetchStatOfTicket(i);
						}
		}).catch((error) => {
			console.error(error);
		});
	}

	fetchStatOfTicket(i) {
		fetch(URL + '/statistics/' + this.state.tickets[i].id, {method:'GET', headers: getAuth()})
					.then((response) => response.json())
					.then((responseJson) => {
						this.setState({
							isLoading: false,
							stats: responseJson
						}, function() { });
						this.state.ticketsWithStats[this.state.ticketsWithStats.length] = {
							id: this.state.tickets[i].id,
							ticketSummary: this.state.tickets[i].ticketSummary,
							ticketCategory: this.state.tickets[i].ticketCategory,
							ticketStatus: this.state.tickets[i].ticketStatus,
							U: this.state.stats.U,
							UP: this.state.stats.UP,
						};
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

    _renderItem({item}) {
			return (
					<TouchableOpacity
							onPress={()=> this.props.navigation.navigate("Sixth", {id:item.id}) }
							style={styles.buttonLargeContainer}
					>
									<Text style={styles.buttonText}>
										 id:  {item.id}
									 </Text>
									 <Text style={styles.buttonText}>
										summary: {item.ticketSummary}
									 </Text>
									<Text style={styles.buttonText}>
										category: {item.ticketCategory}
									</Text>
									{this._getTicketStatus({item})}
									 <Text style={styles.buttonText}>
									accepted: {item.U}
									</Text>
									<Text style={styles.buttonText}>
									positive: {item.UP}
									</Text>
						</TouchableOpacity>
			);
  	}

		_getTicketStatus({item}) {
			ticketstatus = item.ticketStatus;
			switch (item.ticketStatus){
				case 'OPEN':
					return (	<Text style={styles.buttonText}>
										status: {item.ticketStatus}
				 						</Text> );
				case 'ACCEPTED':
					return (	<Text style={styles.buttonTextAccepted}>
										status: {item.ticketStatus}
				 						</Text> );
				case 'IN PROGRESS':
					return (	<Text style={styles.buttonInProgress}>
										status: {item.ticketStatus}
										</Text> );
				case 'PROCESSED':
					return (	<Text style={styles.buttonCompleted}>
										status: {item.ticketStatus}
										</Text> );
				case 'FINISHED':
					return (	<Text style={styles.buttonCompleted}>
										status: {item.ticketStatus}
										</Text> );
				default:
					return(
									<Text style={styles.buttonText}>
									status: {item.ticketStatus}
									</Text>
					);
			}
		}

    render() {
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
                  data={this.state.ticketsWithStats}
                  renderItem={this._renderItem.bind(this)}
                 keyExtractor={(item, index) => index}
                />
        </View>
        );
    }
  }
