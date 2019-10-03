const state = {
  projectInfo:{},
  dataType:[
    {
      label:"SHAPEFILE",
      name:"shapefile",
    },{
      label:"GEOTIFF",
      name:"geotiff",
    },{
      label:"GEOJSON",
      name:"geojson",
    },{
      label:"CSV",
      name:"csv",
    },{
      label:"NETCDF",
      name:"netcdf",
    },{
      label:"TXT",
      name:"txt",
    },{
      label:"SDAT",
      name:"sdat",
    },{
      label:"UDX",
      name:"udx",
    },{
      label:"OTHER",
      name:"other",
    }
  ],
  itemType:[
    {
      label:"int",
      name:"int",
    },{
      label:"double",
      name:"double",
    },{
      label:"string",
      name:"string",
    },{
      label:"boolean",
      name:"boolean"
    }
  ]
}

const mutations = {
  //* xSet是一个通用set方法
  xSet:function(state,obj){
    function _set(obj){
      for(var key in obj){
        if(typeof key === 'object'){
          _set(obj[key])
        }else{
          state[key] = obj[key]
        }
      }
    }

    _set(obj);
  },
}

const actions = {
  //
}

export default {
  namespaced: true,//* 启用命名空间
  state,
  mutations,
  actions
}
