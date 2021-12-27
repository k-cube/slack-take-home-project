# android-code-exercise
Slack Take Home Project.

# Third part libraries used:
- Glide: for loading netword images
- AndroidX Preference: for working with Shared Preference Storage

# Key decisions made
- Use data binding in the adapter to show the results of Search API
- Keep the MVP pattern and added logics with respect to that
- Optimize Network call by adding Debounce time of 200 Miliseconds and adding cache layer to show the already fetched key words.
- Used Shared Preferences for the secondary living list of denied list since the raw file is not editable
- Added loading stated as well as error messages for different states
