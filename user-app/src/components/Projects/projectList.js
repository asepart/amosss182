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

    async onAddProject() {

         const { navigate } = this.props.navigation;
          navigate("Third", { name: "JoinProject" })
    
       } 
      
        
render() {
    var {params} = this.props.navigation.state;
    return (
      <View style={styles.container}>
          <TouchableOpacity 
         onPress={this.onAddProject.bind(this)} 
          style={styles.buttonLargeContainer}>
          
              <Text style={styles.buttonText}>Add Project</Text>
        
          </TouchableOpacity>
          <View>
          <FlatList
					style={styles.textLarge}
					data={this.state.userProjects}
					renderItem={this._renderProjects.bind(this)}
					 keyExtractor={(item, index) => index}
				/>   
          </View>    

      </View>
    );
  }
}