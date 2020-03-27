<?php
	function main_features() {
		global $feature_data;
		write_feature_data($feature_data);
	}
	
	function write_feature_data($data) {
		$first = true;
		$categories = array_keys($data);

		foreach($categories as $i => $key) {
			$category = $data[$key];
			$next = $i >= (sizeof($categories) - 1) ? "" : $categories[$i + 1];
			write_category($key, $category, $first, $next);
			$first = false;
		}
	}

	function write_category($name, $category, $displayed, $next) {
		$class = 'feature-category';
		if($displayed)
			$class = "$class active-category";

		div($class, array('data-category' => $name ));
			div('section-header');
				write(ucfirst($name));
			pop();
			$count = sizeof($category);

			if($count > 0) {
				$first = $category[0];
				if(array_key_exists('addon', $first) && $first['addon'])
					$count--;
			}

			div('feature-count');
				write("($count Features)");
			pop();

			if($count == 0) {
				push('h1');
					write('No features here yet!');
				pop();
			} else {
				div('feature-list');
					usort($category, "cmp_features");
					foreach($category as $k => $feature)
						write_feature($feature, $name);
				pop();
			}

			if(strlen($next)) {
				div('data-category-changer std-button button-long button-next button-features', array('data-category' => $next));
					div('button-title');
						write('Next: ' . ucfirst($next));
					pop();
				pop();	
			} else {
				div('data-entry-changer std-button button-long button-next button-features', array('data-entry' => 'download'));
					div('button-title');
						write('Download Quark');
					pop();
				pop();	
			}
		pop();
	}

	function write_feature($feature, $category_name) {
		if(array_key_exists('addon', $feature) && $feature['addon']) {
			div('info');
				write('Note: This module is an Addon, and is not included with base Quark.<br>');
				write("If you want these features, you can <a href='{$feature['addon_url']}''>install it separately</a>, just like you wuould Quark.");
			pop();
			return;
		}

		div('feature');
			div('feature-image');
				img("img/features/$category_name/{$feature['image']}");
			pop();

			div('feature-info');
				div('feature-header');
					div('feature-title');
						write($feature['name']);
						if(array_key_exists('removed', $feature) && $feature['removed']) {
							span('feature-removed');
								write(' (Removed)');
							pop();
						}
					pop();

					div('feature-version');
						write($feature['versions']);
					pop();
				pop();

				$has_expand = array_key_exists('expand', $feature);

				div('feature-desc' . ($has_expand ? ' feature-desc-with-expand' : ''));
					write_desc($feature['desc']);
				pop();

				if($has_expand) {
					div('feature-expand');
						write_desc($feature['expand']);
					pop();

					div('std-button feature-expand-button');
						div('button-title');
							write('More Info');
						pop();
					pop();
				}
			pop();
		pop();
	}

	function write_desc($arr) {
		$list = false;
		foreach($arr as $i => $line) {
			$fchar = $line[0];
			$rem = substr($line, 1);

			if($fchar == '*') {
				if(!$list)
					push('ul');

				$list = true;
				push('li');
					write($rem);
				pop();
			} else {
				if($list) {
					pop();
					$list = false;
				}

				switch ($fchar) {
					case '#':
						div('feature-desc-header');
							write($rem);
						pop();
						break;
					case '!':
						img($rem);
						break;
					case '-':
						write('<hr>');
						break;
					default:
						p($line);
						break;
				}
			}
		}

		if($list)
			pop();
	}

	function cmp_features($f1, $f2) {
		if((array_key_exists('addon', $f1) && $f1['addon']) || (array_key_exists('addon', $f2) && $f2['addon']))
			return 0;	

		return strcmp($f1['name'], $f2['name']);
	}

	main_features();
?>