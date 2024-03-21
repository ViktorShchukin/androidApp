package com.example.test.tools.dto

import com.example.test.model.Data
import com.example.test.model.Experiment

data class ExperimentWithDataDTO(val experiment: Experiment,
                                 val dataList: List<Data>){
}