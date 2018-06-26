import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity} from 'react-native';
import styles from '../Login/Design';
import {setState} from '../Login/state';
import {StackNavigator,} from 'react-navigation';

export default class ProjectList extends Component {
    static navigationOptions= {
		title: 'Project Overview',
		headerStyle: {
			backgroundColor:'#5daedb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
    } 
}