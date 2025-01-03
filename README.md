# Kisan Swap
**KisanSwap** is a marketplace app designed to help farmers in India buy and sell second-hand agricultural equipment like motors, pumps, tractors, threshers, cables, transformers, and more. The platform caters to Hindi-speaking farmers, focusing on a simple, user-friendly interface for those with low literacy levels. With a focus on accessibility and ease of use, KisanSwap aims to make buying and selling farm equipment seamless and convenient.

## Features
### Second hand Seller Features:
- User can Upload second products with details such as:
    - Short description
    - Long description
    - Price
    - PrimaryCategory
    - SecondaryCategory
    - TertiaryCategory
    - Location
    - Photos and videos
- View a list of uploaded products.
- Monitor impressions and clicks on products.
- Delete products.
- Edit products.
- Chat with buyers on WhatsApp.
- Can sign-in with Google or Phone number.

### Buyer Features:
- **Home Screen**:
    - View a list of products with:
        - Main photo
        - Short description
        - Price
        - Distance from the user’s location
        - Product location
    - Filter products by:
        - Category
        - Price range
        - Distance
    - See brand new products at top with similar categories and specifications.
- **Buy Screen**:
    - View detailed product information with:
        - Horizontal image and video pager
        - Save or share the product
        - Contact options:
            - Call the seller
            - Chat on WhatsApp
            - View location on Google Maps
- Can sign-in with Google only.

### Store Owner Features:
- Create a store with:
    - Store name
    - Store description
    - Store logo
    - Store location
    - products: 
        - Short description
        - Long description
        - Price
        - PrimaryCategory
        - SecondaryCategory
        - TertiaryCategory
        - Location
        - Photos and videos
    - Monitor impressions and clicks on products.
    - Delete products.
    - Edit products.
- sign-in with Google or Phone number.

### Skilled Worker Features:
- Create a profile with:
    - Name
    - Skills
      - Experience
    - Location
    - Photos and videos
- View a list of skilled workers.
- Monitor impressions and clicks on profile.
- Delete profile.
- Edit profile.
- Chat with clients on WhatsApp.
- Can sign-in with Google or Phone number.

## Screen looks and working:

### Splash Screen:
- Logo with app name
- Loading animation
- App name
- App version

### Onboarding/Intro Screen:
- Background image
- Scaffold with:
    - Horizontal pager with automatic scrolling:
        - Column with:
            - Image
            - Title
            - Description
    - Bottom navigation bar:
        - Dots for each screen
        - Skip button
        - Next button

### Home Screen:
- Top app bar:
    - Row with:
        - App Logo
        - App name
        - notification icon
- Scaffold with:
    - menu icon (floating action button at top right)
       - filter icon and name
           - category
           - Specifications(power, capacity, etc.)
       - sort icon and name
           - distance
           - price
           - upload date and time
    - Scrollable Column with:
        - Row with:
           - Search bar
           - Mic icon
        - Row with:
          - Categories in tabs
              - Row of icons:
                  - Category icon 
                  - Category name
        - Horizontal pager with automatic scrolling:
            - New product:
                - Image
                - Short description
                - Price
                - Distance
                - Location
        - LazyGrid (with 2 columns):
            - Product card:
                - Image
                - Short description
                - Price
                - Distance from buyer
                - Location (address name)
                - time since upload
- Bottom navigation bar:
    - if user is Buyer or Second-hand seller
         - Home icon and name (Home Screen)
         - Saved items icon and name (Saved items Screen)
         - Sell product icon and name (Sell product Screen)
         - MyProducts icon and name (MyProducts Screen)
         - Account icon and name (Account Screen)
    - if user is Store owner
         - Home icon and name (Home Screen)
         - Store icon and name (MyStore Screen)
         - Sell product icon and name (Sell product Screen)
         - MyProducts icon and name (MyProducts Screen)
         - Account icon and name (Account Screen)
    - if user is Skilled worker
         - Home icon and name (Home Screen)
         - Works and endorsements(Work Screen)
         - Sell product icon and name (Sell product Screen)
         - MyProducts icon and name (MyProducts Screen)
         - Account icon and name (Account Screen)

### Store Public Screen:
- Top app bar:
    - Row with:
        - Back icon
        - Screen name
- Scaffold with:
    - Column with:
        - Store name
        - Store description
        - Store logo
        - Store location
        - Row with:
            - Search bar
            - Mic icon
        - Row with:
            - Categories in tabs
                - Row of icons:
                    - Category icon
                    - Category name
        - Row with:
            - filter icon and name
                - category
                - Specifications(power, capacity, etc.)
            - sort icon and name
                - price
                - specifications (power, capacity, etc.)
        - LazyGrid (with 2 columns):
            - Product card:
                - Image
                - Short description
                - Price
                - Distance from buyer
                - Location (address name)
                - time since upload
- Bottom navigation bar

### MyStore Screen(only for store owner):
- Top app bar:
    - Row with:
        - Back icon
        - Screen name
- Scaffold with:
    - Column with:
        - Store name
        - Store description
        - Store logo
        - Store location
        - Row with:
            - Search bar
            - Mic icon
        - Row with:
            - Categories in tabs
                - Row of icons:
                    - Category icon
                    - Category name
        - Row with:
            - filter icon and name
                - category
                - Specifications(power, capacity, etc.)
            - sort icon and name
                - price
                - specifications (power, capacity, etc.)
        - LazyGrid (with 2 columns):
            - Product card:
                - Image
                - Short description
                - List(Map(Price, specifications(e.g. power, capacity, etc.)))
                - Location (address name)
                - time since upload
        - Add product button
- Bottom navigation bar

### PhoneAuthentication Screen:
- Top app bar:
    - Row with:
        - Back icon
        - Screen name
- Scaffold
   - Row
      - Country code selector
      - text field(phone number)
   - OTP field
- Bottom navigation bar

### Add new product Screen(only for store owner):
- Top app bar:
    - Row with:
        - Back icon
        - Screen name
- Scaffold with:
- Column with:
    - Row with:
        - Search bar
        - Mic icon
    - Row with:
        - Categories in tabs
            - Row of icons:
                - Category icon
                - Category name
    - Product Uploading        
       - Specifications (there should be a row which contain units of maps like (selector Power, capacity etc.)"Power", (unit selector e.g. Hp, KW etc.)"HP", "price" just above the editing row of map )
           - List
              - Map (3-4 selectors or textfield in a row)(like "Power", "5", "30000")
                 - Price
                 - Specifications(Power, capacity,    voltage, brand, model etc.)
       - Short description
       - Long description
    - LazyRow:
        - image(round cornered with small cross icon at top right to remove the image)
        - add image button
    - LazyRow:
        - video-thumbnails(round cornered with cross icon at top right and play icon at center)
        - add video
    - Upload button
- Bottom navigation bar 

### Work Screen:
- Top app bar:
    - Row with:
        - Back icon
        - Screen name
- Scaffold with:
    - Column with:
        - Profile image
        - Name
        - Skills
        - Experience
        - Location
        - LazyColumn:
            - Work card:
                - Client name
                - Client location
                - Client contact
                - Short description
                - charges
                - time since upload
                - Edit icon
                - Delete icon
- Bottom navigation bar

### Edit work Screen:
- Top app bar:
    - Row with:
        - Back icon
        - Screen name
- Scaffold with:
    - Column with:
        - Profile image
        - Name
        - Skills
        - Experience
        - Location
        - LazyColumn:
            - Work card:
                - Client name
                - Client location
                - Client contact
                - Short description
                - charges
                - time since upload
                - Edit icon
                - Delete icon
        - Add work button
- Bottom navigation bar

### SavedItems Screen:
- Top app bar:
    - Row with:
        - Back icon
        - Screen name
- Scaffold with:
    - LazyGrid (with 2 columns):
        - Product card:
            - Image
            - Short description
            - Price
            - Distance from buyer
            - Location (address name)
            - time since upload
            - Edit icon
            - Delete icon
- Bottom navigation bar

### ProductDetail Screen:
- Top app bar:
    - Row with:
        - Back icon
        - Screen name
        - share icon
- Scaffold with:
    - Floating action button:
        - Save icon
    - Column with:
        - Horizontal pager with automatic scrolling:
            - Image
            - Video
        - Column with:
            - Row with:
                - Price
                - time since upload
            - Short description
            - Specifications
                 - Power
                 - Capacity
                 - Brand
                 - Model
                 - Year of purchase
            - Long description
            - Row with:
                - Distance from buyer
                - Location (address name)
        - Row with:
            - Call icon
            - Chat icon
            - location icon
- Bottom navigation bar

### SellProduct Screen:
- Top app bar:
    - Row with:
        - Back icon
        - Screen name
- Scaffold with:
    - Column with:
        - Row with:
            - Search bar
            - Mic icon
        - Row with:
            - Categories in tabs
                - Row of icons:
                    - Category icon
                    - Category name
        - Specifications (as select fields)
            - Power
            - Capacity
            - Brand
            - Model
            - Year of purchase
        - price
        - Short description
        - Long description
        - Location change icon button
        - LazyRow:
           - image(round cornered with cross icon at top right)
        - LazyRow:
           - video-thumbnails(round cornered with cross icon at top right and play icon at center)
        - Upload button
- Bottom navigation bar

### MyProducts Screen:
- Top app bar:
    - Row with:
        - Back icon
        - Screen name
- Scaffold with  
   - LazyColumn:
        - Product card:
           - Image
           - Short description
           - Price
           - Distance from buyer
           - Location (address name)
           - time since upload
           - if user is Store owner or premium Second-hand seller
               - no. of impressions
               - no. of clicks
               - no. of chats
               - no. of calls
           - Edit icon
           - Delete icon

### EditProduct Screen:
- Top app bar:
    - Row with:
        - Back icon
        - Screen name
- Scaffold with:
    - Column with:
        - Row with:
            - Search bar
            - Mic icon
        - Row with:
            - Categories in tabs
                - Row of icons:
                    - Category icon
                    - Category name
        - Specifications (as select fields)
            - Power
            - Capacity
            - Brand
            - Model
            - Year of purchase
        - price
        - Short description
        - Long description
        - Location change icon button
        - LazyRow:
           - image(round cornered with cross icon at top right)
        - LazyRow:
           - video-thumbnails(round cornered with cross icon at top right and play icon at center)
        - Update button
- Bottom navigation bar

### Account Screen:
- Top app bar:
    - Row with:
        - Back icon
        - Screen name
- Scaffold with:
    - Column with       
         - Profile image
         - Row:
           - UserType(Second-hand seller, Buyer, Store owner, Skilled worker)
           - Edit icon
         - Row with:
           - Name
           - Edit icon
         - Row with:
           - Phone number
           - Edit icon
         - Row with:
           - Email
         - Row with:
           - Address
           - Edit icon
         - Saved Items
         - Your Products
         - Sign out
         - Delete account
- Bottom navigation bar

### Future Features:
- Advice and tips from other farmers.
- In app chatting feature

## Technologies Used
- **Jetpack Compose**: For building the user interface with code.
- **FlutterFlow**: For building the user interface using drag and drop.
- **Firebase Firestore**: For database and real-time syncing.
- **Google Maps API**: For location-based features.

## Architecture
The app follows **Clean Architecture**, with three main layers:
1. **Presentation Layer**:
    - Handles the user interface and navigation (Jetpack Compose screens).
2. **Domain Layer**:
    - Manages business logic and use cases(model and viewModel).
3. **Data Layer**:
    - Communicates with Firebase Firestore for CRUD operations(repository).

# KisanSwap
A marketplace and contact point for all the farmers to exchange goods and services.
