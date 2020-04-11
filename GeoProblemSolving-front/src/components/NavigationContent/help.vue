<style scoped>
img {
  width: 100%;
  height: auto;
}
</style>
<template>
  <div style="">
    <Menu theme="light" active-name="Overview" :open-names="['1']" @on-select="showPage"
      style="width:280px;position:fixed">
      <MenuItem name="Overview">
      <Icon type="ios-paper" />
      Overview
      </MenuItem>
      <MenuItem name="HBP">
      <Icon type="ios-paper" />
      How to build a project?
      </MenuItem>
      <MenuItem name="HSC">
      <Icon type="ios-paper" />
      How to start a compare?
      </MenuItem>
      <MenuItem name="HAR">
      <Icon type="ios-paper" />
      How to achieve Reproducibility?
      </MenuItem>
    </Menu>
    <div style="margin-right:50px;margin-left:290px;">
      <div>
        <mavon-editor :subfield="false" defaultOpen="preview" :toolbarsFlag="false" v-model="activedPage"
          style="z-index:1;" />
      </div>
    </div>
  </div>
</template>
<script>
export default {
  created() {
    // this.getHelpImg();
    this.activedPage = this.overview;
  },
  data() {
    return {
      imageList: [],
      activedPage: this.overview,
      overview: `## Overview
Terrestrial carbon cycle models are important tools for simulating carbon exchange; however, there are still significant uncertainties in the simulation results of different models. Model-data intercomparison has therefore been widely recognized as an effective approach to evaluate model performance and acquire a more reliable understanding of the terrestrial carbon cycle. Although considerable efforts have been made in establishing model intercomparison projects (MIPs), existing MIPs still experience limitations in supporting dispersal researchers collaboratively working online and ensuring the reproducibility of model experiments. This web-based platform helps researchers to construct participatory intercomparison (PIC) projects.

## PIC Strategy
![PIC Framwork](http://ww1.sinaimg.cn/large/5cf483fdgy1gdq4tmcvksj214a0tqn0m.jpg)
### 1. PIC topic
- The PIC topic is used to help participants codesign **backgrounds**, **goals**, and comparison **protocols**.

>To initialize a PIC project, it is necessary to define the motivation and objectives since they are the most intuitive information to attract different researchers to join and become participants. Two corresponding parts, the backgrounds and goals, are therefore designed in a PIC topic. In the establishment process of a PIC project, the backgrounds and goals are first defined by the creator, and then all participants can propose ideas to update them. After confirming the backgrounds and goals, the intercomparison protocols of a PIC project need to be formulated.
### 2. PIC instance
- The PIC instance is used to help participants provide **models**, **observations** and **benchmark** data as reusable services.

>PIC instances are the fundamental comparable elements of model performance evaluation. According to existing MIPs, model performance can be evaluated in the following ways: intermodel comparison, model-observation comparison and model-benchmark comparison. Consequently, there are three types of PIC instances :(1) PIC instances of the observation type, which should be created by uploading associated observation data sets.(2) PIC instances of the benchmark type, which should be created by uploading associated benchmark data sets.(3) PIC instances of the model type, which should be created by uploading the associated model and configuring the parameters to run the model.

### 3. PIC task
- The PIC task is used to help participants formulate comparison workflows and acquire customized comparison results.
>In a PIC project, according to the designed protocols, various PIC tasks can be created and undertaken. Since there are multiple different variables in the model outputs, the metrics to be compared (e.g., GPP, NEP, NPP, and leaf area index (LAI)) must be first defined when creating a PIC task. Then, different PIC instances should be selected and imported to the PIC task. After the metrics and related PIC instances are determined, detailed comparison logic should be designed. A workflow-based method is proposed to help participants design customized comparison logic of a PIC task (detailed in Supplementaty 2.3).
## Advantage
- By using the proposed strategy, a PIC project can be easily created and maintained by distributed participants.
- Reusability of the models and data can be achieved through the proposed service-based wrapping method.
- Reproducibility of model-data comparison experiments can be achieved through the workflow-based comparison method.`,
      hbp: `## How to build a project?

### Step 1: Register
![home_register](http://ww1.sinaimg.cn/large/5cf483fdgy1gdq63l5l7ij215q0j4qt4.jpg)
![register](http://ww1.sinaimg.cn/large/5cf483fdgy1gdq6572a2hj214t0j6q3u.jpg)

### Step 2: Create Project
![create project 1](http://ww1.sinaimg.cn/large/5cf483fdgy1gdq6ewsb5nj21540h9q53.jpg)
![create project 2](http://ww1.sinaimg.cn/large/5cf483fdgy1gdq6fony3fj213p0fpaao.jpg)
![create project 3](http://ww1.sinaimg.cn/large/5cf483fdgy1gdq6g6r9dij213c0gojs8.jpg)`,
      hsc: `## How to start a compare?
### Step 1:
![start task 1](http://ww1.sinaimg.cn/large/5cf483fdgy1gdq6qtpw61j21660iv416.jpg)
### Step 2:
![start task 2](http://ww1.sinaimg.cn/large/5cf483fdgy1gdq7est2lpj21380ky75j.jpg)
### Step 3:
![start task 3](http://tvax2.sinaimg.cn/large/5cf483fdgy1gdq7fvg92rg218g0jwkbr.gif)`,
      har: `## How to achieve reproducibility?
### Step 1:
![reproducibility](http://tvax1.sinaimg.cn/large/5cf483fdgy1gdq7qndydlj215l0i70vf.jpg)
### Step 2:
![reproducibility2](http://tvax2.sinaimg.cn/large/5cf483fdgy1gdq7r4e0rgj21530kdacc.jpg)
### Step 3:
![reproducibility3](http://tvax1.sinaimg.cn/large/5cf483fdgy1gdq7rhaihmj217u0jtjta.jpg)`,
    };
  },
  methods: {
    showPage(name) {
      if (name === "Overview") {
        this.activedPage = this.overview;
      } else if (name === "HBP") {
        this.activedPage = this.hbp;
      } else if (name === "HSC") {
        this.activedPage = this.hsc;
      } else if (name === "HAR") {
        this.activedPage = this.har;
      }
    },
    getHelpImg() {
      for (var i = 1; i < 8; i++) {
        var imgUrl = require("@/assets/images/Help/" + "help" + i + ".png");
        this.imageList.push(imgUrl);
      }
    }
  }
};
</script>
