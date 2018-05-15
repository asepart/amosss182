import { StyleSheet} from 'react-native';

export default styles=StyleSheet.create({
   
  container: {
      flex: 1,
      padding: 20,
      backgroundColor: '#0c3868',
  
    },

    container2: {
      flex: 1,
      flexDirection: 'row',
      justifyContent: 'space-between',
      padding: 20,
      backgroundColor: '#0c3868',
  
    },

    containerAlign: {
      flex: 1,
      padding: 20,
      backgroundColor: '#0c3868',
      //alignItems moves items to upper center
      alignItems: 'center',
      //justifyContent moves items to center of page
      justifyContent: 'center'
  
    },
    containerBottomAlign: {
      flex: 1,
      padding: 20,
      backgroundColor: '#0c3868',
      //alignItems moves items to upper center
      alignItems: 'baseline',
      //justifyContent moves items to center of page
      justifyContent: 'center'
  
    },

    //Text input boxes 
    input: {
      height: 40,
      width: 200,
      color: '#FFF',
      backgroundColor: '#14639e',
      marginBottom: 10,
      paddingHorizontal: 10
    },
    // Text input box span over whole horizontal
    inputLong: {
      height: 40,
      width: 350,
      color: '#FFF',
      backgroundColor: '#14639e',
      marginBottom: 10,
      paddingVertical: 10,
      paddingHorizontal: 10
    },
    //normal button
    buttonContainer: {
      backgroundColor: '#009fca',
      paddingVertical: 15,
      width: 200,
      
    },
    //button spanning whole horizontal
    buttonLargeContainer: {
      backgroundColor: '#009fca',
      paddingVertical: 15,
      width: 350,
      
    },

    buttonText: {
      textAlign: 'center',
      color: '#FFF',
      fontWeight: '700'
  
    },
    //add button
    addButton: {
       alignItems:'center',
       justifyContent:'center',
       width:60,
       height:60,
       backgroundColor:'#009fca',
       borderRadius:60,
    
    },
    error: {
      color: 'red'
    },

    textLarge: {
     // textAlign: 'center',
      color: '#FFF',
      fontSize: 20
    },

    text: {
      color: '#FFF',
      fontSize: 15
    }
   
  });