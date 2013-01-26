/* 
 * This is the original Prolog implementation.
 * It is provided here only as a reference.
 */
  
test :-
	disable_trace, test1,
	disable_trace, test2,
	disable_trace, test3.

test1 :-
	ExpectedResultList = 
		[subscriber_feed('@Alice',[article(1001,'$ESPN','Tigers sign Prince Fielder.'),article(1002,'$ESPN','Peyton Manning home team locker to be used by arch-nemesis Tom Brady?'),article(2001,'$FOX','Newt nabs South Carolina.'),article(3001,'$CNN','Obama visits Michigan.'),article(3002,'$CNN','Obama urges congress to act in election year.'),article(4001,'$MSNBC','Mitt makes moves in Florida.'),article(4002,'$MSNBC','Celebrities attending Super Bowl parties.')]),subscriber_feed('@Bob',[article(1001,'$ESPN','Tigers sign Prince Fielder.'),article(2001,'$FOX','Newt nabs South Carolina.'),article(3001,'$CNN','Obama visits Michigan.'),article(4001,'$MSNBC','Mitt makes moves in Florida.')]),subscriber_feed('@Chris',[article(3001,'$CNN','Obama visits Michigan.'),article(3002,'$CNN','Obama urges congress to act in election year.'),article(4001,'$MSNBC','Mitt makes moves in Florida.')]),subscriber_feed('@Pat',[article(1001,'$ESPN','Tigers sign Prince Fielder.'),article(1002,'$ESPN','Peyton Manning home team locker to be used by arch-nemesis Tom Brady?'),article(2001,'$FOX','Newt nabs South Carolina.'),article(4001,'$MSNBC','Mitt makes moves in Florida.')])]
	,
	all_subscriber_feeds(ActualResultList),
	enable_trace,
	trace('Test 1: ExpectedResultList = '), trace_nl, trace(ExpectedResultList), trace_nl,
	trace('Test 1: ActualResultList = '), trace_nl, trace(ActualResultList), trace_nl,
	ExpectedResultList = ActualResultList, !,
	trace('Test 1 Passes!'), trace_nl.
test1 :-
	!, 
	trace('Test 1 Fails.'), trace_nl,
	fail.	

test2 :-
	ExpectedResultList = 
		[provider_subscribers('$CNN',['@Alice','@Bob','@Chris']),provider_subscribers('$ESPN',['@Alice','@Bob','@Pat']),provider_subscribers('$FOX',['@Alice','@Bob','@Pat']),provider_subscribers('$MSNBC',['@Alice','@Bob','@Chris','@Pat'])]
	,
	all_provider_readers(ActualResultList),
	enable_trace,
	trace('Test 2: ExpectedResultList = '), trace_nl, trace(ExpectedResultList), trace_nl,
	trace('Test 2: ActualResultList = '), trace_nl, trace(ActualResultList), trace_nl,
	ExpectedResultList = ActualResultList, !,
	trace('Test 2 Passes!'), trace_nl.
test2 :-
	!, 
	trace('Test 2 Fails.'), trace_nl,
	fail.

test3 :-	
	ExpectedResultList = 
		[article_reaches(1001,['@Alice','@Bob','@Pat']),article_reaches(1002,['@Alice','@Pat']),article_reaches(2001,['@Alice','@Bob','@Pat']),article_reaches(2002,[]),article_reaches(3001,['@Alice','@Bob','@Chris']),article_reaches(3002,['@Alice','@Chris']),article_reaches(4001,['@Alice','@Bob','@Chris','@Pat']),article_reaches(4002,['@Alice'])]
	,
	all_article_readers(ActualResultList),
	enable_trace,
	trace('Test 3: ExpectedResultList = '), trace_nl, trace(ExpectedResultList), trace_nl,
	trace('Test 3: ActualResultList = '), trace_nl, trace(ActualResultList), trace_nl,
	ExpectedResultList = ActualResultList, !,
	trace('Test 3 Passes!'), trace_nl.
test3 :-
	!, 
	trace('Test 3 Fails.'), trace_nl,
	fail.
