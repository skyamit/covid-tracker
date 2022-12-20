// added all substring
List<String> list = new ArrayList<>();
for(int i=0;i<n;i++){
	for(int j = i+1; j<n;j++){
		String curr = word.substring(i,j+1);
		int mostFrequent = mostFrequentCount(s);
		if(curr.length()<=mostFrequent/2 && curr.length()>=2)
			list.add(curr);
	}
}

Collections.sort(list,(A,B)->(A.length()==B.length()?A.compareTo(B):A.length()-B.length()));

return list.get(0);

public int mostFrequentCount(String s){
	int[] arr = new int[26];
	int max= 0;
	for(char c : s.toCharArray()){
		arr[c-'a']++;
		max = Math.max(max, arr[c-'a']);
	}

	return max;

}
-------------------

boolean[] visited;

List<List<Integer>> list= new ArrayList<>();
HashSet<Integer> winners = new HashSet<>();

/// inside main
public static void main(){
	visited = new int[n];

	recursion(arr,0);

	for(List<Integer> curr : list){
		int winner = findWinner(curr, 0, -1l,-1);
	}

	return set.size();
}

public int findWinner(List<Integer> list,int index,long power, int winner){
	if(index==list.size()){
		winners.add(winner);
		return;
	}

	if(power<list.get(index)){
		findWinner(list,index+1,power + list.get(index), index);
	}
	else if(power>list.get(index)){
		findWinner(list,index+1,power + list.get(index), winner);
	}
	else{
		findWinner(list, index+1, power + list.get(index), winner);
		findWinner(list, index+1, power + list.get(index), index);
	}

	return;
}


public void recursion(List<Integer> curr, int count,int n){
	if(count==n){
		list.add(new ArrayList<>(curr));
		return;
	}

	for(int i=0;i<n;i++){
		if(visited[i]==false){
			visited[i] = true;
			curr.add(arr[i]);
			recursion(curr, count + 1, n);
			curr.remove(curr.size()-1);
			visited[i] = false;
		}
	}

	return;
}