function parseModel(mxCells){
    computableModel = {};
    //todo 设置 xml;
    cmpItems=[];
    mxCells.forEach(cell=>{
        //* 找 对比方法
        if(cell.vertex && cell.data.type==="comparison"){
            let itemInfo ={};
            let dataList=[];
            let dependencyList=[];
            itemInfo.cmpMethodId =  cell.data.oid;
            //* 找 instance
            mxCells.forEach(cell2=>{
                if(cell.vertex && cell.data.type==="instance"){
                    
                }
            });

            //* 找边
            // let edges = cell.edges;
            // edges.forEach(edge=>{
            //     let source = edge.source; 
            //     if(source.data.type==="instance"){
            //         let data = {};
            //         data.instanceId = source.data.instanceId;
            //         data.cmpDataId = source.data.cmpDataId;
            //         dataList.push(data);
            //     }else if(source.data.type === "normal"){
            //         let denpendency = {};
            //     }
            // });
        }
    });
}

function test(cell,cmpMethodId){
    let obj = {};
    let dataMethods = [];
    if(cell.data.type === "comparison" && cell.data.oid === cmpMethodId){
        obj.wrightRoute = true;
        return obj;
    }
    if(cell.data.type === "normal"){
        obj.dependencyId = cell.data.oid;
        cell.edges.forEach(edge=>{
            if(edge.target.id!=cell.id&&edge.target.id!=cmpMethodId){
                
            }
        });
    }
}