# White Box Test Documentation

## 1. Test ID: WB_DELETE_001

## 2. Short Description:
This test verifies the `deleteQuestion()` method in the QuestionLogic class, which removes a question from the questions list based on the provided question ID. The method uses a lambda expression with `removeIf()` to iterate through the list and remove all questions matching the given ID.

## 3. Tested Code:
**Method:** `QuestionLogic.deleteQuestion(int questionId)`  
**Location:** `src/controller/QuestionLogic.java` (lines 224-226)

```java
public void deleteQuestion(int questionId) {
    questions.removeIf(q -> q.getId() == questionId);
}
```

## 4. Graph (Control Flow Diagram):

```
[1. START: Entry point]
    ↓
[2. Process: questions.removeIf(lambda)]
    ↓
    ├─→ [3. Decision: List iteration - Is list empty?]
    │       │
    │       ├─→ [TRUE: List is empty]
    │       │       ↓
    │       │   [4. Process: No iteration, no removal]
    │       │       ↓
    │       │   [END: Method completes, list remains empty]
    │       │
    │       └─→ [FALSE: List has elements]
    │               ↓
    │           [5. Process: Iterate through each question]
    │               ↓
    │           [6. Decision: q.getId() == questionId?]
    │               │
    │               ├─→ [TRUE: ID matches]
    │               │       ↓
    │               │   [7. Process: Remove question from list]
    │               │       ↓
    │               │   [Continue iteration or END]
    │               │
    │               └─→ [FALSE: ID doesn't match]
    │                       ↓
    │                   [8. Process: Keep question in list]
    │                       ↓
    │                   [Continue iteration or END]
    │
[9. END: Method exit point]
```

**Node Descriptions:**
- Node 1: START - Method entry point
- Node 2: Process - Execute `questions.removeIf(lambda)` 
- Node 3: Decision - Check if list is empty (implicit in removeIf)
- Node 4: Process - No iteration occurs, list remains unchanged
- Node 5: Process - Iterate through each question in the list
- Node 6: Decision - Check if `q.getId() == questionId` (lambda predicate)
- Node 7: Process - Remove question from list (when predicate is true)
- Node 8: Process - Keep question in list (when predicate is false)
- Node 9: END - Method exit point

**Edges:**
- 1 → 2: Entry to removeIf operation
- 2 → 3: Check if list is empty
- 3 (TRUE) → 4: List empty, no iteration
- 3 (FALSE) → 5: List has elements, start iteration
- 4 → 9: Early exit (no changes)
- 5 → 6: For each question, check predicate
- 6 (TRUE) → 7: ID matches, remove question
- 6 (FALSE) → 8: ID doesn't match, keep question
- 7 → 9: Continue or exit after removal
- 8 → 9: Continue or exit after keeping

## 5. Coverage Type: Statement Coverage (כיסוי פקודות)

This test uses statement coverage to ensure all statements in the method are executed. The test covers:
- Execution of the `removeIf()` method call
- The lambda expression evaluation for each element
- Both paths: when ID matches (removal) and when ID doesn't match (no removal)
- Edge cases: empty list, single element, multiple elements

## 6. Test Case:

**Test Case ID:** WB_DELETE_001_TC1

**Input Values:**
- `questionId`: 2
- `questions` list state: Contains 3 questions with IDs [1, 2, 3]

**Preconditions:**
1. A QuestionLogic object is initialized
2. The questions list contains the following questions:
   - Question with ID = 1, text = "First Question?"
   - Question with ID = 2, text = "Middle Question?"
   - Question with ID = 3, text = "Last Question?"

**Test Steps:**
1. Create a QuestionLogic instance
2. Add three questions with IDs 1, 2, and 3
3. Call `deleteQuestion(2)`
4. Verify the result

**Expected Output:**
- The questions list should contain 2 questions (IDs 1 and 3)
- The question with ID = 2 should be removed
- The method completes without throwing an exception
- List size changes from 3 to 2

**Path in Graph (Edges/Transitions Taken):**
1 → 2 → 3 (FALSE) → 5 → 6 → 7 → 9 (END)

**Detailed Path:**
1. Node 1 → Node 2: Entry to method, execute `removeIf()`
2. Node 2 → Node 3: Check if list is empty (FALSE - list has 3 elements)
3. Node 3 (FALSE) → Node 5: Start iteration through list
4. Node 5 → Node 6: For question with ID=1, check predicate `1 == 2` (FALSE)
5. Node 6 (FALSE) → Node 8: Keep question with ID=1, continue iteration
6. Node 5 → Node 6: For question with ID=2, check predicate `2 == 2` (TRUE)
7. Node 6 (TRUE) → Node 7: Remove question with ID=2
8. Node 5 → Node 6: For question with ID=3, check predicate `3 == 2` (FALSE)
9. Node 6 (FALSE) → Node 8: Keep question with ID=3, continue iteration
10. Node 7/8 → Node 9: Method completes, list now contains [1, 3]

**Edges Covered:**
- 1 → 2: Entry to removeIf operation
- 2 → 3: Check if list is empty
- 3 (FALSE) → 5: List has elements, start iteration
- 5 → 6: Check predicate for each element
- 6 (TRUE) → 7: ID matches, remove question (for ID=2)
- 6 (FALSE) → 8: ID doesn't match, keep question (for ID=1 and ID=3)
- 7 → 9: Exit after removal
- 8 → 9: Exit after keeping elements

## 7. Name of Developer: [Your Name]


