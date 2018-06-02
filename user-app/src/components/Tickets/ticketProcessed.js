import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity} from 'react-native';
import styles from '../Login/Design';
import {
	StackNavigator,
  } from 'react-navigation';
import {URL} from '../Login/const';
import {key} from './keyValid';

export default class TicketProcessing extends Component {

    constructor() {
		super();

		this.state = {
			
		}
    }
    
    render() {
        return (
            <View style={styles.container}>
            </View>
        );

    }
}