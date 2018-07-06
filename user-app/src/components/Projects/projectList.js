import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, FlatList} from 'react-native';
import styles from '../Login/Design';
import {setState} from '../Login/state';
import {URL} from '../Login/const';
import {StackNavigator,} from 'react-navigation';
import {getAuth} from '../Login/auth';
import {getUpdateBoolean, setUpdateBoolean} from '../Login/state';
import {setKey, isValid} from '../Projects/keyValid';

export var projectname = '';
export var projectstatus = '';

export default class ProjectList extends Component {

//setting page title 
static navigationOptions= {
  title: 'Projects',
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
      userProjects: [],
      entryKey: "",
			info: "",
			infoType: {}
		};
	}

    componentDidMount() {
        this.fetchUserProjects();
}

componentDidUpdate() {
    if(getUpdateBoolean() === true) {
      this.fetchUserProjects();
      setUpdateBoolean(false);
    }
  }

    async onAddProject() {

      setUpdateBoolean(true);
      setKey(this.state.entryKey);
  
     if(await isValid()){
        setState({isValid: true});

        this.setState({info: "Project added", infoType: styles.success});
        
        //enable for immediate navigation after pressing join
//        const { navigate } = this.props.navigation;
//        navigate("Fourth", { name: "ProjectInfo" });
  
     } else {
        this.setState({info: "Project not found", infoType: styles.error});
     }
      
    }
       
     
       fetchUserProjects() {
        fetch(URL + '/projects', {method:'GET', headers: getAuth()})
				.then((response) => response.json())
					.then((responseJson) => {
						this.setState({
							userProjects: responseJson
						}, function() {});
					}).catch((error) => {
						console.error(error);
					});
       }       

 _renderProjects({item}) {
    if (item.finished === 'false') {
        projectstatus = 'Project is open';

     return (
        <TouchableOpacity
      onPress={()=> this.props.navigation.navigate("Twelfth", {entryKey:item.entryKey}) }
                     style={styles.buttonLargeContainer}>
                     <Text style={styles.buttonText}>
                    Project Name: {item.name} 
                    </Text>
                    <Text style={styles.buttonText}>
                    Project Status: {projectstatus}
                    </Text>
                      </TouchableOpacity>
     );
    }
 }      
 
updateUser() {
	var { navigate } = this.props.navigation;
	navigate("Thirteenth", { name: "UserInfo" });
}
      
render() {
	var buttonEnabled = (this.state.entryKey !== '');
    return (
      <View style={styles.container}>
      <TextInput 
         onChangeText={(text) => this.setState({entryKey: text})} 
        placeholder="Entry Key" placeholderTextColor="#FFF" underlineColorAndroid="transparent" style={styles.inputLong}/>
          <TouchableOpacity 
          disabled={!buttonEnabled}
         onPress={this.onAddProject.bind(this)} 
          style={styles.buttonLargeContainer}>
          
              <Text style={styles.buttonText}>Join Project</Text>
        
          </TouchableOpacity>
          <Text style={this.state.infoType}>
          {this.state.info}
				</Text>
				<Text/>
          <FlatList
					data={this.state.userProjects}
                    renderItem={this._renderProjects.bind(this)}
					 keyExtractor={(item, index) => index.toString()}
          />   
          <TouchableOpacity
			onPress={this.updateUser.bind(this)}
			style={styles.buttonContainer}>
			<Text style={styles.buttonText}>User Information</Text>
		</TouchableOpacity>
      </View>
    );
  }
}