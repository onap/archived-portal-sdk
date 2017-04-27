appDS2.factory('raptorReportFactory', function($http, $q) {
	return {
		getDefinitionByReportId: function(reportId) {
			return $http({
				method: "GET",
                url: "report/wizard/retrieve_tab_wise_data/Def/"+reportId,			
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: getDefinitionByReportId did not return a valid JSON object.");
				}
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: getDefinitionByReportId callback failed");
			});			
		},	
		getDefinitionInSession: function() {
			return $http({
				method: "GET",
                url: "report/wizard/retrieve_tab_wise_data/Def/InSession",			
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: getDefinitionInSession did not return a valid JSON object.");
				}
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: getDefinitionInSession callback failed");
			});			
		},				

		createNewDefinition: function() {
			return $http({
				method: "GET",
                url: "report/wizard/retrieve_tab_wise_data/Def/Create",			
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: createNewDefinition did not return a valid JSON object.");
				}
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: createNewDefinition callback failed");
			});			
		},
		updateDefinition: function(updatedJson,isUpdate) {
			return $http({
				method: "POST",
                url: (isUpdate?("report/wizard/save_def_tab_data/"+updatedJson.reportId):"report/wizard/save_def_tab_data/Create"),
                data: updatedJson			
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: updateDefinition did not return a valid JSON object.");
				}
				return response.data;
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: updateDefinition callback failed");
			});
		},
		saveNewDefinition: function(updatedJson) {
			return $http({
				method: "POST",
                url: "report/wizard/save_def_tab_data/InSession",
                data: updatedJson			
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: saveNewDefinition did not return a valid JSON object.");
				}
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: saveNewDefinition callback failed");
			});
		},		
		getSqlInSession: function() {
			return $http({
				method: "GET",
                url: "report/wizard/retrieve_tab_wise_data/Sql/InSession",			
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: getSqlInSession did not return a valid JSON object.");
				}
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: getSqlInSession callback failed");
			});			
		},		
		testRunSQL: function(queryJSON) {
			return $http({
				method: "POST",
                url: "report/wizard/retrieve_data/true",
                data: queryJSON			
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: testRunSQL did not return a valid JSON object.");
				}
				return response.data;
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: testRunSQL callback failed");
			});
		},		
		formFieldVerifySQL: function(queryJSON) {
			return $http({
				method: "POST",
                url: "report/wizard/retrieve_data/false",
                data: queryJSON			
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: formFieldVerifySQL did not return a valid JSON object.");
				}
				return response.data;
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: formFieldVerifySQL callback failed");				
			});
		},
		getColumnList: function() {
			return $http({
				method: "GET",
                url: "report/wizard/list_columns",
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: getColumnList did not return a valid JSON object.");
				}
				return response.data;
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: getColumnList callback failed");				
			});
		},
		
		getColumnEditInfoById: function(columnId){
			return $http({
				method: "GET",
                url: "report/wizard/retrieve_tab_wise_data/ColEdit/"+columnId,
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: getColumnEditInfoById did not return a valid JSON object.");
				}
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory:  getColumnEditInfoById callback failed");				
			});			
		},
		saveColumnEditInfo: function(updatedColumnJson){
			return $http({
				method: "POST",
                url: "report/wizard/save_col_tab_data",
                data: updatedColumnJson
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: saveColumnEditInfo did not return a valid JSON object.");
				}
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: saveColumnEditInfo callback failed");
			});			
		},		
		postImportXml: function(importXMLJSON){
			return $http({
				method: "POST",
                url: "report/wizard/import_report",
                data: importXMLJSON
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: importXml did not return a valid JSON object.");
				}
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: importXml callback failed");
			});			
		},
		copyReportById: function(reportId) {
			return $http({
				method: "GET",
                url: "report/wizard/copy_report/"+reportId,			
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: copyReportById did not return a valid JSON object.");
				}
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: copyReportById callback failed");
			});			
		},				
		saveFormFieldEditInfo: function(updatedFormFieldJson){
			return $http({
				method: "POST",
                url: "report/wizard/save_formfield_tab_data",
                data: updatedFormFieldJson
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: saveFormFieldEditInfo did not return a valid JSON object.");
				}
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: saveFormFieldEditInfo callback failed");
			});			
		},
		getFormFieldList: function() {
			return $http({
				method: "GET",
                url: "report/wizard/list_formfields",			
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: getFormFieldList did not return a valid JSON object.");
				}
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: getFormFieldList callback failed");				
			});			
		},
		getFormFieldEditInfoById: function(fieldId){
			return $http({
				method: "GET",
                url: "report/wizard/retrieve_tab_wise_data/FormEdit/"+fieldId,
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: getColumnEditInfoById did not return a valid JSON object.");
				}
				return response.data;
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: getFormFieldEditInfoById callback failed");				
			});			
		},		
		deleteFormFieldById: function(fieldId){
			return $http({
				method: "GET",
                url: "report/wizard/retrieve_tab_wise_data/FormEdit/delete/"+fieldId,
			}).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: deleteFormFieldById did not return a valid JSON object.");
				}
				return response.data;
			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: deleteFormFieldById callback failed");				
			});			
		},		
		getColumns: function() {
			return $http
					.get('raptor.htm?action=report.search.execute&r_page=0')
					.then(function(response) {
						if (typeof response.data === 'object') {
							return response.data;
						} else {
							return $q.reject("raptorReportFactory: getColumns did not return a valid JSON object.");
						}
					}, function(response) {
						// something went wrong
						return $q.reject("raptorReportFactory: getColumns callback failed");				
					});
		},
		
		getSearchData : function() {
			return $http
					.get('raptor.htm?action=report.search.execute&r_page=0')
					.then(function(response) {
						if (typeof response.data === 'object') {
							return response.data;
						} else {
							return $q.reject("raptorReportFactory: getSearchData did not return a valid JSON object.");
						}
					}, function(response) {
						// something went wrong
						return $q.reject("raptorReportFactory: getSearchData callback failed");				
					});
		},

		getSearchDataAtPage : function(pageSearchParameter) {
			return $http
					.get('raptor.htm?action=report.search.execute&r_page='+pageSearchParameter)
					.then(function(response) {
						if (typeof response.data === 'object') {
							return response.data;
						} else {
							return $q.reject("raptorReportFactory: getSearchDataAtPage did not return a valid JSON object.");
						}
					}, function(response) {
						// something went wrong
						return $q.reject("raptorReportFactory: getSearchDataAtPage callback failed");				
					});
		},
		getReportDeleteStatus : function(deleteUrl) {
			return $http.get(deleteUrl).then(function(response) {
				if (typeof response.data === 'object') {
					return response.data;
				} else {
					return $q.reject("raptorReportFactory: getReportDeleteStatus did not return a valid JSON object.");
				}

			}, function(response) {
				// something went wrong
				return $q.reject("raptorReportFactory: getReportDeleteStatus callback failed");				
			});
		}
	};
});
