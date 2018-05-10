import React, {Component} from 'react';
import { StyleSheet, Text, View} from 'react-native';

export default class SecondScreen extends Component {

  static navigationOptions= {
		title: 'SecondScreen',
		headerStyle: {
			backgroundColor:'#8eacbb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
	} 
    render() {
      var {params} = this.props.navigation.state;
      return (
        <View style={styles.container}>
        <Text> Second Screen </Text>
        </View>
      );
    }
  }
  
  const styles = StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: '#34515e',
      alignItems: 'center',
      justifyContent: 'center',
    },
  });