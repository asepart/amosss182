import React, {Component} from 'react';
import { View, StyleSheet, Dimensions } from 'react-native';
import { TabView, TabBar, SceneMap } from 'react-native-tab-view';
import {
	StackNavigator,
  } from 'react-navigation';

const FirstRoute = () => (
  <View style={[styles.container, { backgroundColor: '#0c3868' }]} />
);
const SecondRoute = () => (
  <View style={[styles.container, { backgroundColor: '#0c3868' }]} />
);

const ThirdRoute = () => (
    <View style={[styles.container, { backgroundColor: '#0c3868' }]} />
  );

const FourthRoute = () => (
    <View style={[styles.container, { backgroundColor: '#0c38687' }]} />
  );  

const FifthRoute = () => (
    <View style={[styles.container, { backgroundColor: '#0c3868' }]} />
  );  

export default class TabViewExample extends Component {
    static navigationOptions= {
		title: 'Ticket View',
		headerStyle: {
			backgroundColor:'#5daedb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
	} 

  state = {
    index: 0,
    routes: [
      { key: 'first', title: 'All' },
      { key: 'second', title: 'OPEN' },
      { key: 'third', title: 'IN PROGRESS' },
      { key: 'fourth', title: 'PROCESSED' },
      { key: 'fifth', title: 'COMPLETED' },
    ],
  };

  render() {
    return (
      <TabView
        navigationState={this.state}
        renderScene={SceneMap({
          first: FirstRoute,
          second: SecondRoute,
          third: ThirdRoute,
          fourth: FourthRoute,
          fifth: FifthRoute
        })}
        onIndexChange={index => this.setState({ index })}
        initialLayout={{ width: Dimensions.get('window').width }}
      />
    );
  }
}