import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, FlatList} from 'react-native';
import styles from '../Login/Design';
import {setState} from '../Login/state';
import {StackNavigator,} from 'react-navigation';
import {getAuth} from '../Login/auth';

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

    constructor(props) {
		super(props);
		this.state = {
			isLoading: true,
			userProjects: [],
		};
	}

    componentDidMount() {
        this.fetchUserProjects();
}
    async onAddProject() {

         const { navigate } = this.props.navigation;
          navigate("Third", { name: "JoinProject" })
    
       } 
     
       fetchUserProjects() {
        fetch(URL + '/projects', {method:'GET', headers: getAuth()})
				.then((response) => response.json())
					.then((responseJson) => {
						this.setState({
							isLoading: false,
							userProjects: responseJson
						}, function() {});
					}).catch((error) => {
						console.error(error);
					});
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