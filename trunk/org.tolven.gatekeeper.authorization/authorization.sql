insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/gatekeeper/rs',	'/**',									'tssl,rsauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/gatekeeper/rs',	'/**',									'tssl,rsauthz');

insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/gatekeeper/html', '/index.jsp',							'');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/gatekeeper/html', '/styles/**',							'');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/gatekeeper/html', '/images/**',							'');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/gatekeeper/html', '/public/**',							'');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/gatekeeper/html', '/recoverloginpassword/**',				'');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/gatekeeper/html', '/recoverloginpassword/**',				'');

insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/gatekeeper/html', '/**',									'tssl,tauthc');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/gatekeeper/html', '/**',									'tssl,tauthc');

insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/gatekeeper/ws',	'/**',									'tssl,wsauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/gatekeeper/ws',	'/**',									'tssl,wsauthz');

insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/api',				'/**',									'tssl,rspreauthz,apiaf');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/**',									'tssl,rspreauthz,apiaf');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/api',				'/accounts',							'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/accounts',							'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/api',				'/accountTypes',						'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/accountTypes',						'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/api',				'/application.wadl',					'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/growthChartLoader/createGrowthChart',	'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/api',				'/loader/createTrimHeader',				'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/loader/createTrimHeader',				'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/api',				'/loader/loadApplications',				'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/loader/loadApplications',				'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/api',				'/loader/loadStateNames',				'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/loader/loadStateNames',				'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/loader/packageBody',					'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/api',				'/loader/placeholders',					'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/loader/placeholders',					'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/loader/queueActivateNewTrimHeaders',	'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/loader/storeReport',					'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/loader/tolvenproperties/reset',		'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/api',				'/loader/user/accountUsers',			'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/loader/user/accountUsers',			'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/loader/user/activate',				'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/api',				'/loader/user/details',					'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/loader/user/details',					'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/scheduler/interval',					'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/scheduler/stop',						'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/scheduler/timeout',					'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/api',				'/tolvenproperties/find',				'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/tolvenproperties/find',				'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/api',				'/tolvenproperties/set',				'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/tolvenproperties/set',				'tssl,rspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/api',				'/tolvenproperties/remove',				'tssl,rspreauthz');

insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/**',									'tssl,preauthc');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/Tolven',			'/**',									'tssl,preauthc');

insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/images/**',							'');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/public/**',							'');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/scripts/**',							'');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/styles/**',							'');

insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/vestibule/**',						'tssl,preauthc,entervf,selectvf,exitvf');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/Tolven',			'/vestibule/**',						'tssl,preauthc,entervf,selectvf,exitvf');

insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/ajax/**',								'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/Tolven',			'/ajax/**',								'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/drilldown/**',						'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/Tolven',			'/drilldown/**',						'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/document',							'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/five/**',								'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/Tolven',			'/five/**',								'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/invitation/**',						'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/Tolven',			'/invitation/**',						'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/manage/**',							'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/Tolven',			'/manage/**',							'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/private/**',							'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/Tolven',			'/private/**',							'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/report/**',							'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/Tolven',			'/report/**',							'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/templates/**',						'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/Tolven',			'/templates/**',						'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/Tolven',			'/wizard/**',							'tssl,preauthc,af');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/Tolven',			'/wizard/**',							'tssl,preauthc,af');

insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/ws',				'/**',									'tssl,wspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'POST',	'/ws',				'/**',									'tssl,wspreauthz');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/ws',				'/EchoService',							'');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/ws',				'/DocumentService',						'');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/ws',				'/GeneratorService',					'');
insert into @tablePrefix@tolvenauth (policy, urlMethod, contextPath, url, filters) values ('default', 'GET',	'/ws',				'/TrimService',							'');
